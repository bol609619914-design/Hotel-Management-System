package com.example.hotel.service;

import com.example.hotel.common.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hotel.dto.RoomRequest;
import com.example.hotel.dto.RoomQueryRequest;
import com.example.hotel.entity.Room;
import com.example.hotel.vo.RoomAvailabilityVO;
import java.time.LocalDate;
import java.util.List;

public interface RoomService extends IService<Room> {

    List<Room> listAllRooms();

    Room getDetail(Long id);

    Room create(RoomRequest request);

    Room update(Long id, RoomRequest request);

    void delete(Long id);

    List<RoomAvailabilityVO> listAvailableRooms(LocalDate checkIn, LocalDate checkOut);

    PageResult<Room> pageRooms(RoomQueryRequest request);
}
