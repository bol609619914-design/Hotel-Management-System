package com.example.hotel.dto;

import jakarta.validation.constraints.NotNull;

public record ReservationRoomChangeRequest(
        @NotNull(message = "roomId is required")
        Long roomId,
        String reason
) {
}
