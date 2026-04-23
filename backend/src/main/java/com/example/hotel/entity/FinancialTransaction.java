package com.example.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("financial_transaction")
public class FinancialTransaction {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reservationId;

    private String reservationNo;

    private String transactionType;

    private BigDecimal amount;

    private String direction;

    private String remark;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
