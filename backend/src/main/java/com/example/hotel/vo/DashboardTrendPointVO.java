package com.example.hotel.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardTrendPointVO(
        LocalDate date,
        BigDecimal revenue,
        Long arrivals,
        Long departures,
        Long bookedCount
) {
}
