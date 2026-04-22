package com.example.hotel.controller;

import com.example.hotel.common.ApiResponse;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.CustomerReservationRequest;
import com.example.hotel.service.CustomerPortalService;
import com.example.hotel.vo.ReservationVO;
import com.example.hotel.entity.RoomType;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerPortalController {

    private final CustomerPortalService customerPortalService;

    public CustomerPortalController(CustomerPortalService customerPortalService) {
        this.customerPortalService = customerPortalService;
    }

    @GetMapping("/room-types")
    public ApiResponse<List<RoomType>> roomTypes() {
        return ApiResponse.success(customerPortalService.listRoomTypes());
    }

    @GetMapping("/reservations")
    public ApiResponse<PageResult<ReservationVO>> myReservations(@RequestParam(required = false) Long pageNo,
                                                                 @RequestParam(required = false) Long pageSize) {
        return ApiResponse.success(customerPortalService.pageMyReservations(pageNo, pageSize));
    }

    @PostMapping("/reservations")
    public ApiResponse<ReservationVO> createReservation(@Valid @RequestBody CustomerReservationRequest request) {
        return ApiResponse.success("customer reservation created", customerPortalService.createReservation(request));
    }
}
