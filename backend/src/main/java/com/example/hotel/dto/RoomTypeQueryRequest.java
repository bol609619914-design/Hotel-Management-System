package com.example.hotel.dto;

public record RoomTypeQueryRequest(
        Long pageNo,
        Long pageSize,
        String keyword
) {
    public PageQuery pageQuery() {
        return new PageQuery(pageNo, pageSize);
    }
}
