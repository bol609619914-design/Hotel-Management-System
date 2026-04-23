package com.example.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.NotificationQueryRequest;
import com.example.hotel.entity.NotificationMessage;
import com.example.hotel.entity.Reservation;
import com.example.hotel.vo.NotificationVO;

public interface NotificationService extends IService<NotificationMessage> {

    void createBookingSuccessNotification(Reservation reservation);

    void syncUpcomingNotifications();

    PageResult<NotificationVO> pageNotifications(NotificationQueryRequest request);

    void markAsRead(Long id);
}
