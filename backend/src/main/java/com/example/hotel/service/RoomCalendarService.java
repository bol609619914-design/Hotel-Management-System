package com.example.hotel.service;

import com.example.hotel.vo.RoomCalendarRowVO;
import java.time.LocalDate;
import java.util.List;

public interface RoomCalendarService {

    List<RoomCalendarRowVO> getCalendar(LocalDate startDate, Integer days, Integer floor);
}
