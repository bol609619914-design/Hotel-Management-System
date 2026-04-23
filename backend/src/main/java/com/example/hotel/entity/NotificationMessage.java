package com.example.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("notification_message")
public class NotificationMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String category;

    private String title;

    private String content;

    private String relatedType;

    private Long relatedId;

    private String targetRole;

    private String status;

    @TableField("scheduled_at")
    private LocalDateTime scheduledAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("read_at")
    private LocalDateTime readAt;
}
