package com.example.hotel.security;

import com.example.hotel.entity.AdminUser;
import com.example.hotel.entity.CustomerUser;
import com.example.hotel.service.AdminUserService;
import com.example.hotel.service.CustomerUserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserService adminUserService;
    private final CustomerUserService customerUserService;

    public AdminUserDetailsService(AdminUserService adminUserService,
                                   CustomerUserService customerUserService) {
        this.adminUserService = adminUserService;
        this.customerUserService = customerUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserService.getByUsername(username);
        if (adminUser != null) {
            return User.withUsername(adminUser.getUsername())
                    .password(adminUser.getPassword())
                    .roles(adminUser.getRole())
                    .disabled(!"ACTIVE".equals(adminUser.getStatus()))
                    .build();
        }
        CustomerUser customerUser = customerUserService.getByUsername(username);
        if (customerUser != null) {
            return User.withUsername(customerUser.getUsername())
                    .password(customerUser.getPassword())
                    .roles("CUSTOMER")
                    .disabled(!"ACTIVE".equals(customerUser.getStatus()))
                    .build();
        }
        throw new UsernameNotFoundException("User not found");
    }
}
