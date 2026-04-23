package com.example.hotel.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.hotel.common.PageResult;
import com.example.hotel.common.SecurityUtil;
import com.example.hotel.dto.CreateReservationRequest;
import com.example.hotel.dto.PageQuery;
import com.example.hotel.dto.ReservationExtendRequest;
import com.example.hotel.dto.ReservationQueryRequest;
import com.example.hotel.dto.ReservationRoomChangeRequest;
import com.example.hotel.dto.ReservationStatusRequest;
import com.example.hotel.entity.Guest;
import com.example.hotel.entity.Reservation;
import com.example.hotel.entity.Room;
import com.example.hotel.entity.RoomType;
import com.example.hotel.exception.BusinessException;
import com.example.hotel.mapper.ReservationMapper;
import com.example.hotel.service.FinancialTransactionService;
import com.example.hotel.service.GuestService;
import com.example.hotel.service.NotificationService;
import com.example.hotel.service.OperationLogService;
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

    private static final DateTimeFormatter SERIAL_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final GuestService guestService;
    private final RoomService roomService;
    private final RoomTypeService roomTypeService;
    private final FinancialTransactionService financialTransactionService;
    private final OperationLogService operationLogService;
    private final NotificationService notificationService;

    public ReservationServiceImpl(GuestService guestService,
                                  RoomService roomService,
                                  RoomTypeService roomTypeService,
                                  FinancialTransactionService financialTransactionService,
                                  OperationLogService operationLogService,
                                  NotificationService notificationService) {
        this.guestService = guestService;
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
        this.financialTransactionService = financialTransactionService;
        this.operationLogService = operationLogService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public ReservationVO createReservation(CreateReservationRequest request) {
        validateReservationRequest(request, null);

        Guest guest = guestService.createOrUpdateGuest(request.guestName(), request.phone(), request.idCard());
        Reservation reservation = new Reservation();
        reservation.setReservationNo(generateReservationNo());
        applyReservationRequest(reservation, request, guest.getId());
        reservation.setCreatedAt(LocalDateTime.now());
        save(reservation);
        financialTransactionService.recordReservationCreated(reservation);
        notificationService.createBookingSuccessNotification(reservation);
        operationLogService.logReservationAction(
                "CREATE_RESERVATION",
                reservation,
                reservation.getRoomId(),
                "创建预订订单",
                null,
                buildReservationSnapshot(reservation)
        );

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
        Reservation before = copyReservation(reservation);
        Guest guest = guestService.createOrUpdateGuest(request.guestName(), request.phone(), request.idCard());
        applyReservationRequest(reservation, request, guest.getId());
        reservation.setStatus(before.getStatus());
        reservation.setActualCheckInTime(before.getActualCheckInTime());
        reservation.setActualCheckOutTime(before.getActualCheckOutTime());
        updateById(reservation);
        financialTransactionService.recordReservationAdjustment(before, reservation, "修改订单");
        operationLogService.logReservationAction(
                "UPDATE_RESERVATION",
                reservation,
                reservation.getRoomId(),
                "更新预订信息",
                buildReservationSnapshot(before),
                buildReservationSnapshot(reservation)
        );

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
        operationLogService.logReservationAction(
                "DELETE_RESERVATION",
                reservation,
                reservation.getRoomId(),
                "删除预订订单",
                buildReservationSnapshot(reservation),
                null
        );
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
        Reservation before = copyReservation(reservation);

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
        if ("CHECKED_IN".equals(reservation.getStatus())) {
            financialTransactionService.recordStatusEvent(reservation, "CHECK_IN_CONFIRMED", "办理入住");
        } else if ("CHECKED_OUT".equals(reservation.getStatus())) {
            financialTransactionService.recordStatusEvent(reservation, "CHECKOUT_SETTLEMENT", "办理退房结算");
        } else if ("CANCELLED".equals(reservation.getStatus())) {
            financialTransactionService.recordStatusEvent(reservation, "BOOKING_CANCELLED", "预订已取消");
        }
        operationLogService.logReservationAction(
                "STATUS_CHANGE",
                reservation,
                reservation.getRoomId(),
                "订单状态从 " + currentStatus + " 变更为 " + reservation.getStatus(),
                buildReservationSnapshot(before),
                buildReservationSnapshot(reservation)
        );
        return getReservationDetail(id);
    }

    @Override
    @Transactional
    public ReservationVO extendReservation(Long id, ReservationExtendRequest request) {
        Reservation reservation = requireOperableReservation(id);
        if (!request.checkOutDate().isAfter(reservation.getCheckOutDate())) {
            throw new BusinessException("new checkOutDate must be later than current checkOutDate");
        }
        Long conflictCount = baseMapper.countReservationConflicts(
                reservation.getRoomId(),
                reservation.getCheckInDate(),
                request.checkOutDate(),
                reservation.getId()
        );
        if (conflictCount != null && conflictCount > 0) {
            throw new BusinessException("room is not available for the extended dates");
        }
        Reservation before = copyReservation(reservation);
        ReservationChargeBreakdownVO charges = calculateCharges(
                reservation.getRoomId(),
                reservation.getCheckInDate(),
                request.checkOutDate(),
                reservation.getBreakfastFee(),
                reservation.getExtraBedFee(),
                reservation.getDepositAmount(),
                reservation.getCouponAmount()
        );
        reservation.setCheckOutDate(request.checkOutDate());
        reservation.setRoomFee(charges.roomFee());
        reservation.setBreakfastFee(charges.breakfastFee());
        reservation.setExtraBedFee(charges.extraBedFee());
        reservation.setDepositAmount(charges.depositAmount());
        reservation.setCouponAmount(charges.couponAmount());
        reservation.setTotalAmount(charges.totalAmount());
        updateById(reservation);
        financialTransactionService.recordReservationAdjustment(before, reservation, "续住");
        operationLogService.logReservationAction(
                "EXTEND_STAY",
                reservation,
                reservation.getRoomId(),
                "续住至 " + request.checkOutDate(),
                buildReservationSnapshot(before),
                buildReservationSnapshot(reservation)
        );
        return getReservationDetail(id);
    }

    @Override
    @Transactional
    public ReservationVO changeReservationRoom(Long id, ReservationRoomChangeRequest request) {
        Reservation reservation = requireOperableReservation(id);
        if (request.roomId().equals(reservation.getRoomId())) {
            return getReservationDetail(id);
        }
        Room currentRoom = roomService.getById(reservation.getRoomId());
        Room targetRoom = roomService.getById(request.roomId());
        if (targetRoom == null) {
            throw new BusinessException("target room does not exist");
        }
        if ("MAINTENANCE".equals(targetRoom.getStatus())) {
            throw new BusinessException("target room under maintenance cannot be assigned");
        }
        Long conflictCount = baseMapper.countReservationConflicts(
                targetRoom.getId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getId()
        );
        if (conflictCount != null && conflictCount > 0) {
            throw new BusinessException("target room is not available for the reservation dates");
        }

        Reservation before = copyReservation(reservation);
        reservation.setRoomId(targetRoom.getId());
        ReservationChargeBreakdownVO charges = calculateCharges(
                reservation.getRoomId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getBreakfastFee(),
                reservation.getExtraBedFee(),
                reservation.getDepositAmount(),
                reservation.getCouponAmount()
        );
        reservation.setRoomFee(charges.roomFee());
        reservation.setTotalAmount(charges.totalAmount());
        updateById(reservation);

        if ("CHECKED_IN".equals(reservation.getStatus())) {
            currentRoom.setStatus("AVAILABLE");
            targetRoom.setStatus("OCCUPIED");
            roomService.updateById(currentRoom);
            roomService.updateById(targetRoom);
        }

        financialTransactionService.recordReservationAdjustment(before, reservation, "换房");
        operationLogService.logReservationAction(
                "ROOM_CHANGE",
                reservation,
                reservation.getRoomId(),
                "换房为 " + targetRoom.getRoomNumber() + (request.reason() == null || request.reason().isBlank() ? "" : "，原因：" + request.reason()),
                buildReservationSnapshot(before),
                buildReservationSnapshot(reservation)
        );
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

    private String generateReservationNo() {
        return "RES" + SERIAL_FORMATTER.format(LocalDateTime.now()) + String.format("%03d", Math.floorMod(System.nanoTime(), 1000));
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

    private Reservation requireOperableReservation(Long id) {
        Reservation reservation = getById(id);
        if (reservation == null) {
            throw new BusinessException("reservation does not exist");
        }
        if ("CHECKED_OUT".equals(reservation.getStatus()) || "CANCELLED".equals(reservation.getStatus())) {
            throw new BusinessException("completed reservation cannot be modified");
        }
        return reservation;
    }

    private Reservation copyReservation(Reservation source) {
        Reservation target = new Reservation();
        target.setId(source.getId());
        target.setReservationNo(source.getReservationNo());
        target.setGuestId(source.getGuestId());
        target.setRoomId(source.getRoomId());
        target.setCheckInDate(source.getCheckInDate());
        target.setCheckOutDate(source.getCheckOutDate());
        target.setGuestCount(source.getGuestCount());
        target.setRoomFee(source.getRoomFee());
        target.setBreakfastFee(source.getBreakfastFee());
        target.setExtraBedFee(source.getExtraBedFee());
        target.setDepositAmount(source.getDepositAmount());
        target.setCouponAmount(source.getCouponAmount());
        target.setTotalAmount(source.getTotalAmount());
        target.setStatus(source.getStatus());
        target.setChannel(source.getChannel());
        target.setSpecialRequest(source.getSpecialRequest());
        target.setCreatedAt(source.getCreatedAt());
        target.setActualCheckInTime(source.getActualCheckInTime());
        target.setActualCheckOutTime(source.getActualCheckOutTime());
        return target;
    }

    private String buildReservationSnapshot(Reservation reservation) {
        return String.format(
                "reservationNo=%s,status=%s,roomId=%s,checkIn=%s,checkOut=%s,total=%s,operator=%s/%s",
                reservation.getReservationNo(),
                reservation.getStatus(),
                reservation.getRoomId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getTotalAmount(),
                SecurityUtil.currentUsername(),
                SecurityUtil.currentRole()
        );
    }
}
