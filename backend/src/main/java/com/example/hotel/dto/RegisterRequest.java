package com.example.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "username is required")
        @Size(min = 4, max = 32, message = "username length must be between 4 and 32")
        String username,
        @NotBlank(message = "displayName is required")
        @Size(max = 32, message = "displayName length must not exceed 32")
        String displayName,
        @NotBlank(message = "password is required")
        @Size(min = 6, max = 32, message = "password length must be between 6 and 32")
        String password,
        @NotBlank(message = "confirmPassword is required")
        String confirmPassword,
        String role
) {
}
