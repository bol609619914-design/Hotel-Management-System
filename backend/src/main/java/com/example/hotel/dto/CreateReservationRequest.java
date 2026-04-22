package com.example.hotel.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateReservationRequest(
        @NotBlank(message = "guestName is required")
        String guestName,
        @NotBlank(message = "phone is required")
        @Pattern(regexp = "^1\\d{10}$", message = "phone format is invalid")
        String phone,
        @NotBlank(message = "idCard is required")
        String idCard,
        @NotNull(message = "roomId is required")
        Long roomId,
        @NotNull(message = "checkInDate is required")
        @FutureOrPresent(message = "checkInDate must be today or later")
        LocalDate checkInDate,
        @NotNull(message = "checkOutDate is required")
        LocalDate checkOutDate,
        @NotNull(message = "guestCount is required")
        @Min(value = 1, message = "guestCount must be at least 1")
        @Max(value = 6, message = "guestCount cannot exceed 6")
        Integer guestCount,
        BigDecimal breakfastFee,
        BigDecimal extraBedFee,
        BigDecimal depositAmount,
        BigDecimal couponAmount,
        String channel,
        String specialRequest
) {
}
