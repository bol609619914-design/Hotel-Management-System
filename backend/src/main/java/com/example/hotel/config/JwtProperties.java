package com.example.hotel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hotel.jwt")
public record JwtProperties(
        String secret,
        long expireHours
) {
}
