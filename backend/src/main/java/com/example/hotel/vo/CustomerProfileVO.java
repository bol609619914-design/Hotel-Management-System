package com.example.hotel.vo;

public record CustomerProfileVO(
        Long id,
        String username,
        String phone,
        String displayName,
        String memberLevel,
        String role
) {
}
