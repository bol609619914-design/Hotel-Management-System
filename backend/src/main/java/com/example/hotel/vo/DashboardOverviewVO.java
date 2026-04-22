package com.example.hotel.vo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewVO {

    private Long availableRooms;

    private Long inHouseReservations;

    private Long todayArrivals;

    private BigDecimal lastThirtyDaysRevenue;
}
