package com.example.hotel.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.hotel.common.PageResult;
import com.example.hotel.common.SecurityUtil;
import com.example.hotel.dto.NotificationQueryRequest;
import com.example.hotel.entity.NotificationMessage;
import com.example.hotel.entity.Reservation;
import com.example.hotel.exception.BusinessException;
import com.example.hotel.mapper.NotificationMessageMapper;
import com.example.hotel.mapper.ReservationMapper;
import com.example.hotel.service.NotificationService;
import com.example.hotel.vo.NotificationVO;
import com.example.hotel.vo.ReminderSourceVO;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMessageMapper, NotificationMessage>
        implements NotificationService {

    private final ReservationMapper reservationMapper;

    public NotificationServiceImpl(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;
    }

    @Override
    public void createBookingSuccessNotification(Reservation reservation) {
        NotificationMessage message = new NotificationMessage();
        message.setCategory("BOOKING_SUCCESS");
        message.setTitle("新预订已创建");
        message.setContent("订单 " + reservation.getReservationNo() + " 已创建，请留意后续入住安排。");
        message.setRelatedType("RESERVATION");
        message.setRelatedId(reservation.getId());
        message.setTargetRole("STAFF");
        message.setStatus("UNREAD");
        message.setScheduledAt(LocalDateTime.now());
        message.setCreatedAt(LocalDateTime.now());
        save(message);
    }

    @Override
    public void syncUpcomingNotifications() {
        createReminderBatch(reservationMapper.selectUpcomingCheckInReminders(), "UPCOMING_CHECKIN", "即将入住提醒",
                reminder -> "订单 " + reminder.getReservationNo() + " · " + reminder.getGuestName() + " 将于 "
                        + reminder.getBusinessDate() + " 到店，房间 " + reminder.getRoomNumber() + " 请提前确认。");
        createReminderBatch(reservationMapper.selectUpcomingCheckOutReminders(), "UPCOMING_CHECKOUT", "即将退房提醒",
                reminder -> "订单 " + reminder.getReservationNo() + " · " + reminder.getGuestName() + " 将于 "
                        + reminder.getBusinessDate() + " 离店，请准备退房结算。");
    }

    @Override
    public PageResult<NotificationVO> pageNotifications(NotificationQueryRequest request) {
        syncUpcomingNotifications();
        Page<NotificationVO> page = new Page<>(request.pageQuery().safePageNo(), request.pageQuery().safePageSize());
        return PageResult.from(baseMapper.selectNotificationPage(page, request, SecurityUtil.currentRole()));
    }

    @Override
    public void markAsRead(Long id) {
        NotificationMessage message = getById(id);
        if (message == null) {
            throw new BusinessException("notification does not exist");
        }
        message.setStatus("READ");
        message.setReadAt(LocalDateTime.now());
        updateById(message);
    }

    private void createReminderBatch(List<ReminderSourceVO> reminders,
                                     String category,
                                     String title,
                                     java.util.function.Function<ReminderSourceVO, String> contentBuilder) {
        for (ReminderSourceVO reminder : reminders) {
            LocalDateTime scheduledAt = reminder.getBusinessDate().atTime(LocalTime.of(9, 0));
            Long duplicateCount = baseMapper.countDuplicate(category, reminder.getReservationId(), "STAFF", scheduledAt);
            if (duplicateCount != null && duplicateCount > 0) {
                continue;
            }
            NotificationMessage message = new NotificationMessage();
            message.setCategory(category);
            message.setTitle(title);
            message.setContent(contentBuilder.apply(reminder));
            message.setRelatedType("RESERVATION");
            message.setRelatedId(reminder.getReservationId());
            message.setTargetRole("STAFF");
            message.setStatus("UNREAD");
            message.setScheduledAt(scheduledAt);
            message.setCreatedAt(LocalDateTime.now());
            save(message);
        }
    }
}
