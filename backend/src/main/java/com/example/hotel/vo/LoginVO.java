package com.example.hotel.vo;

public record LoginVO(
        Long id,
        String username,
        String displayName,
        String role,
        String token
) {
}
