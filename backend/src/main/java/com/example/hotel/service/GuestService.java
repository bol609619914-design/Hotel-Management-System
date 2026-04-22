package com.example.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.GuestQueryRequest;
import com.example.hotel.dto.GuestRequest;
import com.example.hotel.entity.Guest;
import com.example.hotel.vo.GuestProfileVO;

public interface GuestService extends IService<Guest> {

    Guest createOrUpdateGuest(String fullName, String phone, String idCard);

    Guest getByPhone(String phone);

    PageResult<Guest> pageGuests(GuestQueryRequest request);

    Guest getDetail(Long id);

    GuestProfileVO getProfile(Long id);

    Guest create(GuestRequest request);

    Guest update(Long id, GuestRequest request);

    void delete(Long id);
}
