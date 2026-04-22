package com.example.hotel.dto;

public record RoomQueryRequest(
        Long pageNo,
        Long pageSize,
        String keyword,
        Long roomTypeId,
        String status,
        String cleanStatus
) {
    public PageQuery pageQuery() {
        return new PageQuery(pageNo, pageSize);
    }
}
