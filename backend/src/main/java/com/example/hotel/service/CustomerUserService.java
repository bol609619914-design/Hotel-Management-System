package com.example.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hotel.dto.CustomerRegisterRequest;
import com.example.hotel.entity.CustomerUser;

public interface CustomerUserService extends IService<CustomerUser> {

    CustomerUser getByUsername(String username);

    CustomerUser getByPhone(String phone);

    CustomerUser register(CustomerRegisterRequest request);
}
