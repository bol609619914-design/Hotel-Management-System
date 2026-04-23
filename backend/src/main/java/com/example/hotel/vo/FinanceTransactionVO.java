package com.example.hotel.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FinanceTransactionVO {

    private Long id;

    private Long reservationId;

    private String reservationNo;

    private String guestName;

    private String roomNumber;

    private String transactionType;

    private BigDecimal amount;

    private String direction;

    private String remark;

    private LocalDateTime createdAt;
}
