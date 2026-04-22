package com.example.hotel.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.hotel.entity.AdminUser;
import com.example.hotel.service.AdminUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final AdminUserService adminUserService;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(AdminUserService adminUserService, PasswordEncoder passwordEncoder) {
        this.adminUserService = adminUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        long count = adminUserService.count(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, "admin"));
        if (count > 0) {
            return;
        }

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setDisplayName("系统管理员");
        adminUser.setRole("ADMIN");
        adminUser.setStatus("ACTIVE");
        adminUserService.save(adminUser);

        AdminUser frontDeskUser = new AdminUser();
        frontDeskUser.setUsername("frontdesk");
        frontDeskUser.setPassword(passwordEncoder.encode("front123"));
        frontDeskUser.setDisplayName("前台专员");
        frontDeskUser.setRole("FRONT_DESK");
        frontDeskUser.setStatus("ACTIVE");
        adminUserService.save(frontDeskUser);
    }
}
