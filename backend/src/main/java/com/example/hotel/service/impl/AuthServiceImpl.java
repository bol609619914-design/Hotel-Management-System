package com.example.hotel.service.impl;

import com.example.hotel.common.SecurityUtil;
import com.example.hotel.dto.ChangePasswordRequest;
import com.example.hotel.dto.LoginRequest;
import com.example.hotel.dto.RegisterRequest;
import com.example.hotel.entity.AdminUser;
import com.example.hotel.entity.CustomerUser;
import com.example.hotel.exception.BusinessException;
import com.example.hotel.service.AdminUserService;
import com.example.hotel.service.AuthService;
import com.example.hotel.service.CustomerUserService;
import com.example.hotel.util.JwtTokenProvider;
import com.example.hotel.vo.LoginVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AdminUserService adminUserService;
    private final CustomerUserService customerUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AdminUserService adminUserService,
                           CustomerUserService customerUserService,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.adminUserService = adminUserService;
        this.customerUserService = customerUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginVO login(LoginRequest request) {
        AdminUser adminUser = adminUserService.getByUsername(request.username());
        if (adminUser == null || !"ACTIVE".equals(adminUser.getStatus())) {
            throw new BusinessException("Invalid username or password");
        }
        if (!passwordEncoder.matches(request.password(), adminUser.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }

        String token = jwtTokenProvider.generateToken(adminUser.getId(), adminUser.getUsername(), adminUser.getRole());
        return new LoginVO(adminUser.getId(), adminUser.getUsername(), adminUser.getDisplayName(), adminUser.getRole(), token);
    }

    @Override
    public LoginVO currentUser() {
        String username = SecurityUtil.currentUsername();
        AdminUser adminUser = adminUserService.getByUsername(username);
        if (adminUser != null) {
            String token = jwtTokenProvider.generateToken(adminUser.getId(), adminUser.getUsername(), adminUser.getRole());
            return new LoginVO(adminUser.getId(), adminUser.getUsername(), adminUser.getDisplayName(), adminUser.getRole(), token);
        }
        CustomerUser customerUser = customerUserService.getByUsername(username);
        if (customerUser != null) {
            String token = jwtTokenProvider.generateToken(customerUser.getId(), customerUser.getUsername(), "CUSTOMER");
            return new LoginVO(customerUser.getId(), customerUser.getUsername(), customerUser.getDisplayName(), "CUSTOMER", token);
        }
        throw new BusinessException("Unauthorized");
    }

    @Override
    public LoginVO register(RegisterRequest request) {
        AdminUser adminUser = adminUserService.createUser(request, false);
        String token = jwtTokenProvider.generateToken(adminUser.getId(), adminUser.getUsername(), adminUser.getRole());
        return new LoginVO(adminUser.getId(), adminUser.getUsername(), adminUser.getDisplayName(), adminUser.getRole(), token);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }
        String username = SecurityUtil.currentUsername();
        AdminUser adminUser = adminUserService.getByUsername(username);
        if (adminUser == null) {
            throw new BusinessException("Unauthorized");
        }
        if (!passwordEncoder.matches(request.oldPassword(), adminUser.getPassword())) {
            throw new BusinessException("Old password is incorrect");
        }
        adminUser.setPassword(passwordEncoder.encode(request.newPassword()));
        adminUserService.updateById(adminUser);
    }
}
