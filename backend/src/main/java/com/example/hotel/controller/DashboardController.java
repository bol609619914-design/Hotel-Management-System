package com.example.hotel.controller;

import com.example.hotel.common.ApiResponse;
import com.example.hotel.service.DashboardService;
import com.example.hotel.vo.DashboardOverviewVO;
import com.example.hotel.vo.DashboardTrendVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public ApiResponse<DashboardOverviewVO> overview() {
        return ApiResponse.success(dashboardService.getOverview());
    }

    @GetMapping("/trends")
    public ApiResponse<DashboardTrendVO> trends() {
        return ApiResponse.success(dashboardService.getTrends());
    }
}
