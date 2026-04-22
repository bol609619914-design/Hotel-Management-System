package com.example.hotel.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GuestStatsVO(
        Long totalReservations,
        Long completedStays,
        Long bookedStays,
        BigDecimal totalSpent,
        BigDecimal averageSpent,
        LocalDate lastCheckInDate
) {
}
