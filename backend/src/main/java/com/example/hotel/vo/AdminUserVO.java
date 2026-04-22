package com.example.hotel.vo;

public record AdminUserVO(
        Long id,
        String username,
        String displayName,
        String role,
        String status
) {
}
