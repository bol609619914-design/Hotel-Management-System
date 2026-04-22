package com.example.hotel.dto;

public record PageQuery(
        Long pageNo,
        Long pageSize
) {

    public long safePageNo() {
        return pageNo == null || pageNo < 1 ? 1L : pageNo;
    }

    public long safePageSize() {
        if (pageSize == null || pageSize < 1) {
            return 10L;
        }
        return Math.min(pageSize, 100L);
    }
}
