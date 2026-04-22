package com.example.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.RegisterRequest;
import com.example.hotel.dto.UserQueryRequest;
import com.example.hotel.dto.UserRequest;
import com.example.hotel.entity.AdminUser;
import com.example.hotel.exception.BusinessException;
import com.example.hotel.mapper.AdminUserMapper;
import com.example.hotel.service.AdminUserService;
import com.example.hotel.vo.AdminUserVO;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    private static final List<String> ALLOWED_ROLES = List.of("ADMIN", "FRONT_DESK");
    private static final List<String> ALLOWED_STATUS = List.of("ACTIVE", "DISABLED");

    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AdminUser getByUsername(String username) {
        return baseMapper.findByUsername(username);
    }

    @Override
    public AdminUser createUser(RegisterRequest request, boolean privilegedRoleAllowed) {
        validateUniqueUsername(request.username(), null);
        validatePasswords(request.password(), request.confirmPassword());
        String role = normalizeRole(request.role(), privilegedRoleAllowed);

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(request.username().trim());
        adminUser.setDisplayName(request.displayName().trim());
        adminUser.setPassword(passwordEncoder.encode(request.password()));
        adminUser.setRole(role);
        adminUser.setStatus("ACTIVE");
        save(adminUser);
        return adminUser;
    }

    @Override
    public AdminUser createUser(UserRequest request) {
        validateUniqueUsername(request.username(), null);
        if (!StringUtils.hasText(request.password())) {
            throw new BusinessException("Password is required");
        }
        validateRole(request.role());
        validateStatus(request.status());

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(request.username().trim());
        adminUser.setDisplayName(request.displayName().trim());
        adminUser.setPassword(passwordEncoder.encode(request.password()));
        adminUser.setRole(request.role().trim());
        adminUser.setStatus(request.status().trim());
        save(adminUser);
        return adminUser;
    }

    @Override
    public PageResult<AdminUserVO> pageUsers(UserQueryRequest request) {
        UserQueryRequest safeRequest = request == null ? new UserQueryRequest(null, null, null, null) : request;
        Page<AdminUser> page = new Page<>(
                safeRequest.pageQuery() == null ? 1 : safeRequest.pageQuery().safePageNo(),
                safeRequest.pageQuery() == null ? 10 : safeRequest.pageQuery().safePageSize()
        );

        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(safeRequest.keyword())) {
            wrapper.and(query -> query
                    .like(AdminUser::getUsername, safeRequest.keyword())
                    .or()
                    .like(AdminUser::getDisplayName, safeRequest.keyword()));
        }
        wrapper.eq(StringUtils.hasText(safeRequest.role()), AdminUser::getRole, safeRequest.role())
                .eq(StringUtils.hasText(safeRequest.status()), AdminUser::getStatus, safeRequest.status())
                .orderByAsc(AdminUser::getId);

        Page<AdminUser> result = page(page, wrapper);
        Page<AdminUserVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toVO).toList());
        return PageResult.from(voPage);
    }

    @Override
    public AdminUserVO updateUser(Long id, UserRequest request) {
        AdminUser adminUser = getById(id);
        if (adminUser == null) {
            throw new BusinessException("User not found");
        }
        validateUniqueUsername(request.username(), id);
        validateRole(request.role());
        validateStatus(request.status());

        adminUser.setUsername(request.username().trim());
        adminUser.setDisplayName(request.displayName().trim());
        adminUser.setRole(request.role().trim());
        adminUser.setStatus(request.status().trim());
        if (StringUtils.hasText(request.password())) {
            adminUser.setPassword(passwordEncoder.encode(request.password()));
        }
        updateById(adminUser);
        return toVO(adminUser);
    }

    @Override
    public void deleteUser(Long id) {
        AdminUser adminUser = getById(id);
        if (adminUser == null) {
            return;
        }
        if ("admin".equals(adminUser.getUsername())) {
            throw new BusinessException("Default admin account cannot be deleted");
        }
        removeById(id);
    }

    private void validateUniqueUsername(String username, Long currentId) {
        AdminUser existing = getByUsername(username.trim());
        if (existing != null && (currentId == null || !existing.getId().equals(currentId))) {
            throw new BusinessException("Username already exists");
        }
    }

    private void validatePasswords(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new BusinessException("Passwords do not match");
        }
    }

    private String normalizeRole(String role, boolean privilegedRoleAllowed) {
        String targetRole = StringUtils.hasText(role) ? role.trim() : "FRONT_DESK";
        validateRole(targetRole);
        if (!privilegedRoleAllowed && !"FRONT_DESK".equals(targetRole)) {
            return "FRONT_DESK";
        }
        return targetRole;
    }

    private void validateRole(String role) {
        if (!ALLOWED_ROLES.contains(role == null ? null : role.trim())) {
            throw new BusinessException("Unsupported role");
        }
    }

    private void validateStatus(String status) {
        if (!ALLOWED_STATUS.contains(status == null ? null : status.trim())) {
            throw new BusinessException("Unsupported status");
        }
    }

    private AdminUserVO toVO(AdminUser adminUser) {
        return new AdminUserVO(
                adminUser.getId(),
                adminUser.getUsername(),
                adminUser.getDisplayName(),
                adminUser.getRole(),
                adminUser.getStatus()
        );
    }
}
