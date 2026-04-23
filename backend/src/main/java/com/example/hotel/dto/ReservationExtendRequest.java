package com.example.hotel.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationExtendRequest(
        @NotNull(message = "checkOutDate is required")
        @FutureOrPresent(message = "checkOutDate must be today or later")
        LocalDate checkOutDate
) {
}
