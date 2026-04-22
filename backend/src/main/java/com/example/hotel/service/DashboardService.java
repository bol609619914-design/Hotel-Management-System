package com.example.hotel.service;

import com.example.hotel.vo.DashboardOverviewVO;
import com.example.hotel.vo.DashboardTrendVO;

public interface DashboardService {

    DashboardOverviewVO getOverview();

    DashboardTrendVO getTrends();
}
