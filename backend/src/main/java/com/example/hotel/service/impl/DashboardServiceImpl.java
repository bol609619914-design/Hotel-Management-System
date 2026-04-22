package com.example.hotel.service.impl;

import com.example.hotel.mapper.ReservationMapper;
import com.example.hotel.mapper.RoomMapper;
import com.example.hotel.service.DashboardService;
import com.example.hotel.vo.DashboardOverviewVO;
import com.example.hotel.vo.DashboardTrendPointVO;
import com.example.hotel.vo.DashboardTrendVO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final RoomMapper roomMapper;
    private final ReservationMapper reservationMapper;

    public DashboardServiceImpl(RoomMapper roomMapper, ReservationMapper reservationMapper) {
        this.roomMapper = roomMapper;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public DashboardOverviewVO getOverview() {
        Long availableRooms = roomMapper.countAvailableRooms();
        Long inHouseReservations = reservationMapper.countInHouseReservations();
        Long todayArrivals = reservationMapper.countTodayArrivals();
        BigDecimal revenue = reservationMapper.sumLastThirtyDaysRevenue();

        return new DashboardOverviewVO(
                availableRooms == null ? 0L : availableRooms,
                inHouseReservations == null ? 0L : inHouseReservations,
                todayArrivals == null ? 0L : todayArrivals,
                revenue == null ? BigDecimal.ZERO : revenue
        );
    }

    @Override
    public DashboardTrendVO getTrends() {
        List<DashboardTrendPointVO> points = reservationMapper.selectRecentTrendPoints();
        BigDecimal totalRevenue = points.stream()
                .map(DashboardTrendPointVO::revenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageRevenue = points.isEmpty()
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(points.size()), 2, RoundingMode.HALF_UP);
        long arrivals = points.stream().mapToLong(point -> point.arrivals() == null ? 0 : point.arrivals()).sum();
        long departures = points.stream().mapToLong(point -> point.departures() == null ? 0 : point.departures()).sum();

        return new DashboardTrendVO(totalRevenue, averageRevenue, arrivals, departures, points);
    }
}
