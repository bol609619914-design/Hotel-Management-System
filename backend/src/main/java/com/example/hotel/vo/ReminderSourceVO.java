package com.example.hotel.vo;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ReminderSourceVO {

    private Long reservationId;

    private String reservationNo;

    private String guestName;

    private String roomNumber;

    private LocalDate businessDate;
}
