package com.example.hotel.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerLoginRequest(
        @NotBlank(message = "phone is required")
        String phone,
        @NotBlank(message = "password is required")
        String password
) {
}
