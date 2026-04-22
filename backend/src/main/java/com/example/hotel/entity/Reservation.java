package com.example.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("reservation")
public class Reservation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String reservationNo;

    private Long guestId;

    private Long roomId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Integer guestCount;

    private BigDecimal roomFee;

    private BigDecimal breakfastFee;

    private BigDecimal extraBedFee;

    private BigDecimal depositAmount;

    private BigDecimal couponAmount;

    private BigDecimal totalAmount;

    private String status;

    private String channel;

    private String specialRequest;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("actual_check_in_time")
    private LocalDateTime actualCheckInTime;

    @TableField("actual_check_out_time")
    private LocalDateTime actualCheckOutTime;
}
