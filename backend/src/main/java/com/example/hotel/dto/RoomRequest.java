package com.example.hotel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomRequest(
        @NotBlank(message = "roomNumber is required")
        String roomNumber,
        @NotNull(message = "roomTypeId is required")
        Long roomTypeId,
        @NotNull(message = "floor is required")
        @Min(value = 1, message = "floor must be at least 1")
        Integer floor,
        @NotBlank(message = "status is required")
        String status,
        @NotBlank(message = "cleanStatus is required")
        String cleanStatus
) {
}
