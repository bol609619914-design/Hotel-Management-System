package com.example.hotel.dto;

public record NotificationQueryRequest(
        Long pageNo,
        Long pageSize,
        String status,
        String category
) {
    public PageQuery pageQuery() {
        return new PageQuery(pageNo, pageSize);
    }
}
