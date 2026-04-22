package com.example.hotel.controller;

import com.example.hotel.common.ApiResponse;
import com.example.hotel.dto.CustomerLoginRequest;
import com.example.hotel.dto.CustomerRegisterRequest;
import com.example.hotel.service.CustomerPortalService;
import com.example.hotel.vo.CustomerProfileVO;
import com.example.hotel.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer/auth")
public class CustomerAuthController {

    private final CustomerPortalService customerPortalService;

    public CustomerAuthController(CustomerPortalService customerPortalService) {
        this.customerPortalService = customerPortalService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody CustomerLoginRequest request) {
        return ApiResponse.success("customer login success", customerPortalService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<LoginVO> register(@Valid @RequestBody CustomerRegisterRequest request) {
        return ApiResponse.success("customer register success", customerPortalService.register(request));
    }

    @GetMapping("/me")
    public ApiResponse<CustomerProfileVO> me() {
        return ApiResponse.success(customerPortalService.currentProfile());
    }
}
