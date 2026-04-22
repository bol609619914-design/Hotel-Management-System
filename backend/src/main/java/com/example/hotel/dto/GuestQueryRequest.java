package com.example.hotel.dto;

public record GuestQueryRequest(
        Long pageNo,
        Long pageSize,
        String keyword,
        String memberLevel
) {
    public PageQuery pageQuery() {
        return new PageQuery(pageNo, pageSize);
    }
}
