package com.example.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRegisterRequest(
        @NotBlank(message = "displayName is required")
        @Size(max = 32, message = "displayName length must not exceed 32")
        String displayName,
        @NotBlank(message = "phone is required")
        @Pattern(regexp = "^1\\d{10}$", message = "phone format is invalid")
        String phone,
        @NotBlank(message = "password is required")
        @Size(min = 6, max = 32, message = "password length must be between 6 and 32")
        String password,
        @NotBlank(message = "confirmPassword is required")
        String confirmPassword
) {
}
