package com.example.hotel.vo;

import java.math.BigDecimal;
import java.util.List;

public record DashboardTrendVO(
        BigDecimal totalRevenue,
        BigDecimal averageDailyRevenue,
        Long totalArrivals,
        Long totalDepartures,
        List<DashboardTrendPointVO> points
) {
}
