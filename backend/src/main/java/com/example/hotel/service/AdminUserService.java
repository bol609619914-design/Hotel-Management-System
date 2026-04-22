package com.example.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.RegisterRequest;
import com.example.hotel.dto.UserQueryRequest;
import com.example.hotel.dto.UserRequest;
import com.example.hotel.entity.AdminUser;
import com.example.hotel.vo.AdminUserVO;

public interface AdminUserService extends IService<AdminUser> {

    AdminUser getByUsername(String username);

    AdminUser createUser(RegisterRequest request, boolean privilegedRoleAllowed);

    AdminUser createUser(UserRequest request);

    PageResult<AdminUserVO> pageUsers(UserQueryRequest request);

    AdminUserVO updateUser(Long id, UserRequest request);

    void deleteUser(Long id);
}
