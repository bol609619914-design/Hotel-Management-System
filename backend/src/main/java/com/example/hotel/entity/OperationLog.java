package com.example.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reservationId;

    private Long roomId;

    private String operatorUsername;

    private String operatorRole;

    private String actionType;

    private String description;

    private String beforeSnapshot;

    private String afterSnapshot;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
