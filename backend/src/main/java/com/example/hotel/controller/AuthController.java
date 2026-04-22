package com.example.hotel.controller;

import com.example.hotel.common.ApiResponse;
import com.example.hotel.dto.ChangePasswordRequest;
import com.example.hotel.dto.LoginRequest;
import com.example.hotel.dto.RegisterRequest;
import com.example.hotel.service.AuthService;
import com.example.hotel.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("login success", authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<LoginVO> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("register success", authService.register(request));
    }

    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.success("password updated", null);
    }

    @GetMapping("/me")
    public ApiResponse<LoginVO> me() {
        return ApiResponse.success(authService.currentUser());
    }
}
