package com.example.hotel.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OperationLogVO {

    private Long id;

    private Long reservationId;

    private String reservationNo;

    private String roomNumber;

    private String operatorUsername;

    private String operatorRole;

    private String actionType;

    private String description;

    private String beforeSnapshot;

    private String afterSnapshot;

    private LocalDateTime createdAt;
}
