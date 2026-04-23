package com.example.hotel.vo;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ReservationCalendarItemVO {

    private Long reservationId;

    private String reservationNo;

    private Long roomId;

    private String guestName;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String status;
}
