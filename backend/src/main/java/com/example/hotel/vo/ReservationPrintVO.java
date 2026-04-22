package com.example.hotel.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationPrintVO(
        String documentType,
        String reservationNo,
        String guestName,
        String phone,
        String idCard,
        String roomNumber,
        String roomTypeName,
        Integer guestCount,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        String status,
        String channel,
        String specialRequest,
        LocalDateTime actualCheckInTime,
        LocalDateTime actualCheckOutTime,
        ReservationChargeBreakdownVO charges
) {
}
