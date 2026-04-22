package com.example.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("customer_user")
public class CustomerUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String phone;

    private String password;

    private String displayName;

    private String memberLevel;

    private String status;
}
