package com.example.hotel.dto;

public record OperationLogQueryRequest(
        Long pageNo,
        Long pageSize,
        Long reservationId,
        String actionType,
        String operatorUsername
) {
    public PageQuery pageQuery() {
        return new PageQuery(pageNo, pageSize);
    }
}
