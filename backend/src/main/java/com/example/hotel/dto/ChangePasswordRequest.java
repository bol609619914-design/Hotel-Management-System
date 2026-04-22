package com.example.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "oldPassword is required")
        String oldPassword,
        @NotBlank(message = "newPassword is required")
        @Size(min = 6, max = 32, message = "newPassword length must be between 6 and 32")
        String newPassword,
        @NotBlank(message = "confirmPassword is required")
        String confirmPassword
) {
}
