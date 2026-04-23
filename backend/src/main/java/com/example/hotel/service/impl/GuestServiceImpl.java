package com.example.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.GuestQueryRequest;
import com.example.hotel.dto.GuestRequest;
import com.example.hotel.entity.Guest;
import com.example.hotel.exception.BusinessException;
import com.example.hotel.mapper.GuestMapper;
import com.example.hotel.mapper.ReservationMapper;
import com.example.hotel.service.GuestService;
import com.example.hotel.vo.GuestProfileVO;
import com.example.hotel.vo.GuestStatsVO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuestServiceImpl extends ServiceImpl<GuestMapper, Guest> implements GuestService {

    private final ReservationMapper reservationMapper;

    public GuestServiceImpl(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;
    }

    @Override
    public Guest createOrUpdateGuest(String fullName, String phone, String idCard) {
        Guest guestByPhone = baseMapper.findByPhone(phone);
        Guest guestByIdCard = baseMapper.findByIdCard(idCard);
        if (guestByPhone != null && guestByIdCard != null && !guestByPhone.getId().equals(guestByIdCard.getId())) {
            throw new BusinessException("guest phone and idCard belong to different guests");
        }

        Guest guest = guestByPhone != null ? guestByPhone : guestByIdCard;
        if (guest == null) {
            guest = new Guest();
            guest.setFullName(fullName);
            guest.setPhone(phone);
            guest.setIdCard(idCard);
            guest.setMemberLevel("REGULAR");
            guest.setRemark("created by api");
            save(guest);
            return guest;
        }

        guest.setFullName(fullName);
        guest.setPhone(phone);
        guest.setIdCard(idCard);
        updateById(guest);
        return guest;
    }

    @Override
    public Guest getByPhone(String phone) {
        return baseMapper.findByPhone(phone);
    }

    @Override
    public PageResult<Guest> pageGuests(GuestQueryRequest request) {
        Page<Guest> page = new Page<>(request.pageQuery().safePageNo(), request.pageQuery().safePageSize());
        boolean hasKeyword = request.keyword() != null && !request.keyword().isBlank();
        LambdaQueryWrapper<Guest> wrapper = new LambdaQueryWrapper<Guest>()
                .orderByDesc(Guest::getId)
                .and(hasKeyword, q -> q.like(Guest::getFullName, request.keyword())
                        .or()
                        .like(Guest::getPhone, request.keyword())
                        .or()
                        .like(Guest::getIdCard, request.keyword()))
                .eq(request.memberLevel() != null && !request.memberLevel().isBlank(), Guest::getMemberLevel, request.memberLevel());
        Page<Guest> result = page(page, wrapper);
        return PageResult.from(result);
    }

    @Override
    public Guest getDetail(Long id) {
        Guest guest = getById(id);
        if (guest == null) {
            throw new BusinessException("guest does not exist");
        }
        return guest;
    }

    @Override
    public GuestProfileVO getProfile(Long id) {
        Guest guest = getDetail(id);
        Long totalReservations = reservationMapper.countGuestReservations(id);
        Long completedStays = reservationMapper.countGuestCompletedStays(id);
        Long bookedStays = reservationMapper.countGuestBookedStays(id);
        BigDecimal totalSpent = reservationMapper.sumGuestSpent(id);
        BigDecimal averageSpent = completedStays == null || completedStays == 0
                ? BigDecimal.ZERO
                : totalSpent.divide(BigDecimal.valueOf(completedStays), 2, RoundingMode.HALF_UP);

        return new GuestProfileVO(
                guest,
                new GuestStatsVO(
                        totalReservations == null ? 0L : totalReservations,
                        completedStays == null ? 0L : completedStays,
                        bookedStays == null ? 0L : bookedStays,
                        totalSpent == null ? BigDecimal.ZERO : totalSpent,
                        averageSpent,
                        reservationMapper.findGuestLastCheckInDate(id)
                ),
                reservationMapper.selectGuestHistory(id)
        );
    }

    @Override
    @Transactional
    public Guest create(GuestRequest request) {
        validateDuplicate(request, null);
        Guest guest = new Guest();
        apply(guest, request);
        save(guest);
        return guest;
    }

    @Override
    @Transactional
    public Guest update(Long id, GuestRequest request) {
        Guest guest = getDetail(id);
        validateDuplicate(request, id);
        apply(guest, request);
        updateById(guest);
        return guest;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getDetail(id);
        removeById(id);
    }

    private void validateDuplicate(GuestRequest request, Long excludeId) {
        long phoneCount = count(new LambdaQueryWrapper<Guest>()
                .eq(Guest::getPhone, request.phone())
                .ne(excludeId != null, Guest::getId, excludeId));
        if (phoneCount > 0) {
            throw new BusinessException("guest phone already exists");
        }
        long idCardCount = count(new LambdaQueryWrapper<Guest>()
                .eq(Guest::getIdCard, request.idCard())
                .ne(excludeId != null, Guest::getId, excludeId));
        if (idCardCount > 0) {
            throw new BusinessException("guest idCard already exists");
        }
    }

    private void apply(Guest guest, GuestRequest request) {
        guest.setFullName(request.fullName());
        guest.setPhone(request.phone());
        guest.setIdCard(request.idCard());
        guest.setMemberLevel(request.memberLevel());
        guest.setRemark(request.remark());
    }
}
