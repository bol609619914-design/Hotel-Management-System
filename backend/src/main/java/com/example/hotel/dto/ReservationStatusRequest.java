package com.example.hotel.dto;

import jakarta.validation.constraints.NotBlank;

public record ReservationStatusRequest(
        @NotBlank(message = "status is required")
        String status
) {
}
