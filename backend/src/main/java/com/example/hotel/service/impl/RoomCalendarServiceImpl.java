package com.example.hotel.service.impl;

import com.example.hotel.entity.Room;
import com.example.hotel.mapper.ReservationMapper;
import com.example.hotel.service.RoomCalendarService;
import com.example.hotel.service.RoomService;
import com.example.hotel.service.RoomTypeService;
import com.example.hotel.vo.ReservationCalendarItemVO;
import com.example.hotel.vo.RoomCalendarDayVO;
import com.example.hotel.vo.RoomCalendarRowVO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RoomCalendarServiceImpl implements RoomCalendarService {

    private final RoomService roomService;
    private final RoomTypeService roomTypeService;
    private final ReservationMapper reservationMapper;

    public RoomCalendarServiceImpl(RoomService roomService,
                                   RoomTypeService roomTypeService,
                                   ReservationMapper reservationMapper) {
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public List<RoomCalendarRowVO> getCalendar(LocalDate startDate, Integer days, Integer floor) {
        LocalDate rangeStart = startDate == null ? LocalDate.now() : startDate;
        int rangeDays = days == null || days < 1 ? 7 : Math.min(days, 14);
        LocalDate rangeEnd = rangeStart.plusDays(rangeDays);

        Map<Long, String> roomTypeNames = roomTypeService.listAll().stream()
                .collect(Collectors.toMap(item -> item.getId(), item -> item.getName()));
        List<Room> rooms = roomService.listAllRooms().stream()
                .filter(room -> floor == null || floor.equals(room.getFloor()))
                .sorted(Comparator.comparing(Room::getRoomNumber))
                .toList();
        Map<Long, List<ReservationCalendarItemVO>> reservationMap = reservationMapper
                .selectCalendarReservations(rangeStart, rangeEnd)
                .stream()
                .collect(Collectors.groupingBy(ReservationCalendarItemVO::getRoomId));

        List<RoomCalendarRowVO> rows = new ArrayList<>();
        for (Room room : rooms) {
            List<ReservationCalendarItemVO> roomReservations = reservationMap.getOrDefault(room.getId(), List.of());
            List<RoomCalendarDayVO> dayList = new ArrayList<>();
            for (int i = 0; i < rangeDays; i++) {
                LocalDate date = rangeStart.plusDays(i);
                ReservationCalendarItemVO matched = roomReservations.stream()
                        .filter(item -> !date.isBefore(item.getCheckInDate()) && date.isBefore(item.getCheckOutDate()))
                        .findFirst()
                        .orElse(null);
                if ("MAINTENANCE".equals(room.getStatus())) {
                    dayList.add(new RoomCalendarDayVO(date, "MAINTENANCE", null, null, null));
                } else if (matched != null) {
                    dayList.add(new RoomCalendarDayVO(
                            date,
                            "CHECKED_IN".equals(matched.getStatus()) ? "CHECKED_IN" : "BOOKED",
                            matched.getReservationId(),
                            matched.getReservationNo(),
                            matched.getGuestName()
                    ));
                } else {
                    dayList.add(new RoomCalendarDayVO(date, "AVAILABLE", null, null, null));
                }
            }
            rows.add(new RoomCalendarRowVO(
                    room.getId(),
                    room.getRoomNumber(),
                    room.getFloor(),
                    roomTypeNames.getOrDefault(room.getRoomTypeId(), "-"),
                    room.getCleanStatus(),
                    dayList
            ));
        }
        return rows;
    }
}
