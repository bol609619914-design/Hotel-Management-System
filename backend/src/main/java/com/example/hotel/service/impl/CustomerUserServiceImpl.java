package com.example.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.hotel.dto.CustomerRegisterRequest;
import com.example.hotel.entity.CustomerUser;
import com.example.hotel.exception.BusinessException;
import com.example.hotel.mapper.CustomerUserMapper;
import com.example.hotel.service.CustomerUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserServiceImpl extends ServiceImpl<CustomerUserMapper, CustomerUser> implements CustomerUserService {

    private final PasswordEncoder passwordEncoder;

    public CustomerUserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CustomerUser getByUsername(String username) {
        return baseMapper.findByUsername(username);
    }

    @Override
    public CustomerUser getByPhone(String phone) {
        return baseMapper.findByPhone(phone);
    }

    @Override
    public CustomerUser register(CustomerRegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }
        if (getByPhone(request.phone()) != null) {
            throw new BusinessException("Phone already registered");
        }
        CustomerUser customerUser = new CustomerUser();
        customerUser.setUsername(request.phone());
        customerUser.setPhone(request.phone());
        customerUser.setDisplayName(request.displayName().trim());
        customerUser.setPassword(passwordEncoder.encode(request.password()));
        customerUser.setMemberLevel("REGULAR");
        customerUser.setStatus("ACTIVE");
        save(customerUser);
        return customerUser;
    }
}
