package com.example.hotel.dto;

public record UserQueryRequest(
        String keyword,
        String role,
        String status,
        PageQuery pageQuery
) {
}
