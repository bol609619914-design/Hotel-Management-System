package com.example.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hotel.entity.CustomerUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CustomerUserMapper extends BaseMapper<CustomerUser> {

    @Select("select * from customer_user where username = #{username} limit 1")
    CustomerUser findByUsername(String username);

    @Select("select * from customer_user where phone = #{phone} limit 1")
    CustomerUser findByPhone(String phone);
}
