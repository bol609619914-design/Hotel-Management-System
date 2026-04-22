package com.example.hotel.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.CreateReservationRequest;
import com.example.hotel.dto.PageQuery;
import com.example.hotel.dto.ReservationQueryRequest;
import com.example.hotel.dto.ReservationStatusRequest;
import com.example.hotel.entity.Guest;
import com.example.hotel.entity.Reservation;
import com.example.hotel.entity.Room;
import com.example.hotel.entity.RoomType;
import com.example.hotel.exception.BusinessException;
import com.example.hotel.mapper.ReservationMapper;
import com.example.hotel.service.GuestService;
import com.example.hotel.service.ReservationService;
import com.example.hotel.service.RoomService;
import com.example.hotel.service.RoomTypeService;
import com.example.hotel.vo.ReservationChargeBreakdownVO;
import com.example.hotel.vo.ReservationPrintVO;
import com.example.hotel.vo.ReservationVO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    private static final DateTimeFormatter SERIAL_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final GuestService guestService;
    private final RoomService roomService;
    private final RoomTypeService roomTypeService;

    public ReservationServiceImpl(GuestService guestService, RoomService roomService, RoomTypeService roomTypeService) {
        this.guestService = guestService;
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
    }

    @Override
    @Transactional
    public ReservationVO createReservation(CreateReservationRequest request) {
        validateReservationRequest(request, null);

        Guest guest = guestService.createOrUpdateGuest(request.guestName(), request.phone(), request.idCard());
        Reservation reservation = new Reservation();
        reservation.setReservationNo("RES" + SERIAL_FORMATTER.format(LocalDateTime.now()));
        applyReservationRequest(reservation, request, guest.getId());
        reservation.setCreatedAt(LocalDateTime.now());
        save(reservation);

        return getReservationDetail(reservation.getId());
    }

    @Override
    @Transactional
    public ReservationVO updateReservation(Long id, CreateReservationRequest request) {
        Reservation reservation = getById(id);
        if (reservation == null) {
            throw new BusinessException("reservation does not exist");
        }

        validateReservationRequest(request, id);
        Guest guest = guestService.createOrUpdateGuest(request.guestName(), request.phone(), request.idCard());
        applyReservationRequest(reservation, request, guest.getId());
        updateById(reservation);

        return getReservationDetail(id);
    }

    @Override
    public ReservationVO getReservationDetail(Long id) {
        ReservationVO detail = baseMapper.selectReservationDetailById(id);
        if (detail == null) {
            throw new BusinessException("reservation does not exist");
        }
        return detail;
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = getById(id);
        if (reservation == null) {
            throw new BusinessException("reservation does not exist");
        }
        removeById(id);
    }

    @Override
    public List<ReservationVO> listReservationDetails() {
        return baseMapper.selectReservationDetails();
    }

    @Override
    public PageResult<ReservationVO> pageReservations(ReservationQueryRequest request) {
        Page<ReservationVO> page = new Page<>(request.pageQuery().safePageNo(), request.pageQuery().safePageSize());
        return PageResult.from(baseMapper.selectReservationPage(page, request));
    }

    @Override
    public PageResult<ReservationVO> pageReservationsByGuestId(Long guestId, PageQuery pageQuery) {
        Page<ReservationVO> page = new Page<>(pageQuery.safePageNo(), pageQuery.safePageSize());
        return PageResult.from(baseMapper.selectReservationPageByGuestId(page, guestId));
    }

    @Override
    @Transactional
    public ReservationVO updateReservationStatus(Long id, ReservationStatusRequest request) {
        Reservation reservation = getById(id);
        if (reservation == null) {
            throw new BusinessException("reservation does not exist");
        }

        String targetStatus = request.status().toUpperCase();
        String currentStatus = reservation.getStatus();
        if (currentStatus.equals(targetStatus)) {
            return getReservationDetail(id);
        }

        Room room = roomService.getById(reservation.getRoomId());
        if (room == null) {
            throw new BusinessException("room does not exist");
        }

        switch (currentStatus) {
            case "BOOKED" -> handleBookedTransition(reservation, room, targetStatus);
            case "CHECKED_IN" -> handleCheckedInTransition(reservation, room, targetStatus);
            case "CHECKED_OUT", "CANCELLED" -> throw new BusinessException("completed reservation status cannot be changed");
            default -> throw new BusinessException("unsupported reservation status");
        }

        updateById(reservation);
        roomService.updateById(room);
        return getReservationDetail(id);
    }

    @Override
    public ReservationPrintVO getReservationPrint(Long id) {
        return buildPrintDocument(id, "RESERVATION");
    }

    @Override
    public ReservationPrintVO getCheckInPrint(Long id) {
        return buildPrintDocument(id, "CHECK_IN");
    }

    @Override
    public ReservationPrintVO getCheckoutSettlement(Long id) {
        return buildPrintDocument(id, "CHECKOUT_SETTLEMENT");
    }

    private void validateReservationRequest(CreateReservationRequest request, Long excludeId) {
        if (!request.checkOutDate().isAfter(request.checkInDate())) {
            throw new BusinessException("checkOutDate must be later than checkInDate");
        }

        Room room = roomService.getById(request.roomId());
        if (room == null) {
            throw new BusinessException("room does not exist");
        }
        if ("MAINTENANCE".equals(room.getStatus())) {
            throw new BusinessException("room under maintenance cannot be reserved");
        }
        Long conflictCount = baseMapper.countReservationConflicts(
                request.roomId(),
                request.checkInDate(),
                request.checkOutDate(),
                excludeId
        );
        if (conflictCount != null && conflictCount > 0) {
            throw new BusinessException("room is not available for the selected dates");
        }
    }

    private void applyReservationRequest(Reservation reservation, CreateReservationRequest request, Long guestId) {
        ReservationChargeBreakdownVO charges = calculateCharges(
                request.roomId(),
                request.checkInDate(),
                request.checkOutDate(),
                request.breakfastFee(),
                request.extraBedFee(),
                request.depositAmount(),
                request.couponAmount()
        );
        reservation.setGuestId(guestId);
        reservation.setRoomId(request.roomId());
        reservation.setCheckInDate(request.checkInDate());
        reservation.setCheckOutDate(request.checkOutDate());
        reservation.setGuestCount(request.guestCount());
        reservation.setRoomFee(charges.roomFee());
        reservation.setBreakfastFee(charges.breakfastFee());
        reservation.setExtraBedFee(charges.extraBedFee());
        reservation.setDepositAmount(charges.depositAmount());
        reservation.setCouponAmount(charges.couponAmount());
        reservation.setTotalAmount(charges.totalAmount());
        reservation.setStatus("BOOKED");
        reservation.setChannel(request.channel() == null || request.channel().isBlank() ? "DIRECT" : request.channel());
        reservation.setSpecialRequest(request.specialRequest());
    }

    private ReservationChargeBreakdownVO calculateCharges(Long roomId,
                                                          java.time.LocalDate checkInDate,
                                                          java.time.LocalDate checkOutDate,
                                                          BigDecimal breakfastFee,
                                                          BigDecimal extraBedFee,
                                                          BigDecimal depositAmount,
                                                          BigDecimal couponAmount) {
        Room room = roomService.getById(roomId);
        if (room == null) {
            throw new BusinessException("room does not exist");
        }
        RoomType roomType = roomTypeService.getDetail(room.getRoomTypeId());
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (nights <= 0) {
            throw new BusinessException("stay nights must be greater than 0");
        }
        BigDecimal roomFee = roomType.getBasePrice().multiply(BigDecimal.valueOf(nights));
        BigDecimal breakfast = safeMoney(breakfastFee);
        BigDecimal extraBed = safeMoney(extraBedFee);
        BigDecimal deposit = safeMoney(depositAmount);
        BigDecimal coupon = safeMoney(couponAmount);
        BigDecimal total = roomFee
                .add(breakfast)
                .add(extraBed)
                .add(deposit)
                .subtract(coupon)
                .setScale(2, RoundingMode.HALF_UP);
        return new ReservationChargeBreakdownVO(roomFee, breakfast, extraBed, deposit, coupon, total);
    }

    private BigDecimal safeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private ReservationPrintVO buildPrintDocument(Long id, String documentType) {
        Reservation reservationEntity = getById(id);
        if (reservationEntity == null) {
            throw new BusinessException("reservation does not exist");
        }
        ReservationVO reservation = getReservationDetail(id);
        Guest guest = guestService.getDetail(reservation.getGuestId());
        Room actualRoom = roomService.getById(reservationEntity.getRoomId());
        String roomTypeName = actualRoom == null ? "-" : roomTypeService.getDetail(actualRoom.getRoomTypeId()).getName();
        return new ReservationPrintVO(
                documentType,
                reservation.getReservationNo(),
                reservation.getGuestName(),
                reservation.getPhone(),
                guest.getIdCard(),
                reservation.getRoomNumber(),
                roomTypeName,
                reservation.getGuestCount(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getStatus(),
                reservation.getChannel(),
                reservation.getSpecialRequest(),
                reservation.getActualCheckInTime(),
                reservation.getActualCheckOutTime(),
                new ReservationChargeBreakdownVO(
                        reservation.getRoomFee(),
                        reservation.getBreakfastFee(),
                        reservation.getExtraBedFee(),
                        reservation.getDepositAmount(),
                        reservation.getCouponAmount(),
                        reservation.getTotalAmount()
                )
        );
    }

    private void handleBookedTransition(Reservation reservation, Room room, String targetStatus) {
        switch (targetStatus) {
            case "CHECKED_IN" -> {
                reservation.setStatus("CHECKED_IN");
                reservation.setActualCheckInTime(LocalDateTime.now());
                room.setStatus("OCCUPIED");
            }
            case "CANCELLED" -> reservation.setStatus("CANCELLED");
            default -> throw new BusinessException("invalid BOOKED transition");
        }
    }

    private void handleCheckedInTransition(Reservation reservation, Room room, String targetStatus) {
        if (!"CHECKED_OUT".equals(targetStatus)) {
            throw new BusinessException("invalid CHECKED_IN transition");
        }
        reservation.setStatus("CHECKED_OUT");
        reservation.setActualCheckOutTime(LocalDateTime.now());
        room.setStatus("AVAILABLE");
    }
}
