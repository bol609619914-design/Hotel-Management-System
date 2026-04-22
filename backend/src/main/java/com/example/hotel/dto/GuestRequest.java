package com.example.hotel.dto;

import jakarta.validation.constraints.NotBlank;

public record GuestRequest(
        @NotBlank(message = "fullName is required")
        String fullName,
        @NotBlank(message = "phone is required")
        String phone,
        @NotBlank(message = "idCard is required")
        String idCard,
        @NotBlank(message = "memberLevel is required")
        String memberLevel,
        String remark
) {
}
