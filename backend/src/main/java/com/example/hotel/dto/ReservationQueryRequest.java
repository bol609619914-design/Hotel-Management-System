package com.example.hotel.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record ReservationQueryRequest(
        Long pageNo,
        Long pageSize,
        String keyword,
        String status,
        String channel,
        String roomNumber,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate checkInFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate checkInTo
) {
    public PageQuery pageQuery() {
        return new PageQuery(pageNo, pageSize);
    }
}
