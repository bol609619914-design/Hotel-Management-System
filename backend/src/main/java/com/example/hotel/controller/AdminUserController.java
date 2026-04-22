package com.example.hotel.controller;

import com.example.hotel.common.ApiResponse;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.UserQueryRequest;
import com.example.hotel.dto.UserRequest;
import com.example.hotel.entity.AdminUser;
import com.example.hotel.service.AdminUserService;
import com.example.hotel.vo.AdminUserVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ApiResponse<PageResult<AdminUserVO>> page(UserQueryRequest request) {
        return ApiResponse.success(adminUserService.pageUsers(request));
    }

    @PostMapping
    public ApiResponse<AdminUserVO> create(@Valid @RequestBody UserRequest request) {
        AdminUser user = adminUserService.createUser(request);
        return ApiResponse.success("user created", new AdminUserVO(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getRole(),
                user.getStatus()
        ));
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminUserVO> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ApiResponse.success("user updated", adminUserService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ApiResponse.success("user deleted", null);
    }
}
