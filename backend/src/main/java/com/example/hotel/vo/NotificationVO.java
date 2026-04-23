package com.example.hotel.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NotificationVO {

    private Long id;

    private String category;

    private String title;

    private String content;

    private String relatedType;

    private Long relatedId;

    private String targetRole;

    private String status;

    private LocalDateTime scheduledAt;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;
}
