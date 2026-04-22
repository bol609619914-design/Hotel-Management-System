package com.example.hotel.service;

import com.example.hotel.dto.LoginRequest;
import com.example.hotel.dto.ChangePasswordRequest;
import com.example.hotel.dto.RegisterRequest;
import com.example.hotel.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginRequest request);

    LoginVO currentUser();

    LoginVO register(RegisterRequest request);

    void changePassword(ChangePasswordRequest request);
}
