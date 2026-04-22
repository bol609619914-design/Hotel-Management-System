package com.example.hotel.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GuestStayHistoryVO(
        Long reservationId,
        String reservationNo,
        String roomNumber,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BigDecimal totalAmount,
        String status,
        String channel,
        LocalDateTime actualCheckInTime,
        LocalDateTime actualCheckOutTime
) {
}
