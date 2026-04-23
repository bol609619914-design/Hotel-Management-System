package com.example.hotel.vo;

import java.time.LocalDate;

public record RoomCalendarDayVO(
        LocalDate date,
        String status,
        Long reservationId,
        String reservationNo,
        String guestName
) {
}
