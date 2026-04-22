package com.example.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hotel.entity.Guest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GuestMapper extends BaseMapper<Guest> {

    @Select("select * from guest where phone = #{phone} limit 1")
    Guest findByPhone(String phone);

    @Select("select * from guest where id_card = #{idCard} limit 1")
    Guest findByIdCard(String idCard);
}
