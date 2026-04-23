package com.example.hotel.dto;

public record FinanceQueryRequest(
        Long pageNo,
        Long pageSize,
        Long reservationId,
        String transactionType,
        String direction
) {
    public PageQuery pageQuery() {
        return new PageQuery(pageNo, pageSize);
    }
}
