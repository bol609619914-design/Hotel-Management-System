package com.example.hotel.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReservationVO {

    private Long id;

    private String reservationNo;

    private Long guestId;

    private String guestName;

    private String phone;

    private String roomNumber;

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

    private LocalDateTime createdAt;

    private LocalDateTime actualCheckInTime;

    private LocalDateTime actualCheckOutTime;
}
