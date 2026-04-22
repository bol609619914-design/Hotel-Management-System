package com.example.hotel.vo;

import java.math.BigDecimal;

public record ReservationChargeBreakdownVO(
        BigDecimal roomFee,
        BigDecimal breakfastFee,
        BigDecimal extraBedFee,
        BigDecimal depositAmount,
        BigDecimal couponAmount,
        BigDecimal totalAmount
) {
}
