package com.example.hotel.vo;

import java.math.BigDecimal;

public record FinanceSummaryVO(
        Long transactionCount,
        BigDecimal chargeTotal,
        BigDecimal discountTotal,
        BigDecimal refundTotal,
        BigDecimal netTotal
) {
}
