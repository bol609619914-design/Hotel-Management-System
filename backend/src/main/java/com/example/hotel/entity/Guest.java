package com.example.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("guest")
public class Guest {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String fullName;

    private String phone;

    private String idCard;

    private String memberLevel;

    private String remark;
}
