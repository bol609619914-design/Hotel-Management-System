package com.example.hotel.vo;

import java.util.List;

public record RoomCalendarRowVO(
        Long roomId,
        String roomNumber,
        Integer floor,
        String roomTypeName,
        String cleanStatus,
        List<RoomCalendarDayVO> days
) {
}
