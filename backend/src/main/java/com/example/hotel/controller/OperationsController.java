package com.example.hotel.controller;

import com.example.hotel.common.ApiResponse;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.FinanceQueryRequest;
import com.example.hotel.dto.NotificationQueryRequest;
import com.example.hotel.dto.OperationLogQueryRequest;
import com.example.hotel.service.FinancialTransactionService;
import com.example.hotel.service.NotificationService;
import com.example.hotel.service.OperationLogService;
import com.example.hotel.service.RoomCalendarService;
import com.example.hotel.vo.FinanceSummaryVO;
import com.example.hotel.vo.FinanceTransactionVO;
import com.example.hotel.vo.NotificationVO;
import com.example.hotel.vo.OperationLogVO;
import com.example.hotel.vo.RoomCalendarRowVO;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/operations")
public class OperationsController {

    private final RoomCalendarService roomCalendarService;
    private final FinancialTransactionService financialTransactionService;
    private final OperationLogService operationLogService;
    private final NotificationService notificationService;

    public OperationsController(RoomCalendarService roomCalendarService,
                                FinancialTransactionService financialTransactionService,
                                OperationLogService operationLogService,
                                NotificationService notificationService) {
        this.roomCalendarService = roomCalendarService;
        this.financialTransactionService = financialTransactionService;
        this.operationLogService = operationLogService;
        this.notificationService = notificationService;
    }

    @GetMapping("/room-calendar")
    public ApiResponse<List<RoomCalendarRowVO>> roomCalendar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) Integer floor) {
        return ApiResponse.success(roomCalendarService.getCalendar(startDate, days, floor));
    }

    @GetMapping("/financial-transactions")
    public ApiResponse<PageResult<FinanceTransactionVO>> financialTransactions(FinanceQueryRequest request) {
        return ApiResponse.success(financialTransactionService.pageTransactions(request));
    }

    @GetMapping("/financial-summary")
    public ApiResponse<FinanceSummaryVO> financialSummary(FinanceQueryRequest request) {
        return ApiResponse.success(financialTransactionService.getSummary(request));
    }

    @GetMapping("/logs")
    public ApiResponse<PageResult<OperationLogVO>> logs(OperationLogQueryRequest request) {
        return ApiResponse.success(operationLogService.pageLogs(request));
    }

    @GetMapping("/notifications")
    public ApiResponse<PageResult<NotificationVO>> notifications(NotificationQueryRequest request) {
        return ApiResponse.success(notificationService.pageNotifications(request));
    }

    @PutMapping("/notifications/{id}/read")
    public ApiResponse<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.success("notification marked as read", null);
    }
}
