package com.example.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.RoomQueryRequest;
import com.example.hotel.dto.RoomRequest;
import com.example.hotel.entity.Room;
import com.example.hotel.exception.BusinessException;
import com.example.hotel.mapper.ReservationMapper;
import com.example.hotel.mapper.RoomMapper;
import com.example.hotel.service.RoomTypeService;
import com.example.hotel.service.RoomService;
import com.example.hotel.vo.RoomAvailabilityVO;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    private final RoomTypeService roomTypeService;
    private final ReservationMapper reservationMapper;

    public RoomServiceImpl(RoomTypeService roomTypeService, ReservationMapper reservationMapper) {
        this.roomTypeService = roomTypeService;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public List<Room> listAllRooms() {
        return list(new LambdaQueryWrapper<Room>().orderByAsc(Room::getRoomNumber));
    }

    @Override
    public Room getDetail(Long id) {
        Room room = getById(id);
        if (room == null) {
            throw new BusinessException("room does not exist");
        }
        return room;
    }

    @Override
    @Transactional
    public Room create(RoomRequest request) {
        validateRequest(request, null);
        Room room = new Room();
        apply(room, request);
        save(room);
        return room;
    }

    @Override
    @Transactional
    public Room update(Long id, RoomRequest request) {
        Room room = getDetail(id);
        validateRequest(request, id);
        apply(room, request);
        updateById(room);
        return room;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Room room = getDetail(id);
        if ("OCCUPIED".equals(room.getStatus())) {
            throw new BusinessException("occupied room cannot be deleted");
        }
        Long reservationCount = reservationMapper.countByRoomId(id);
        if (reservationCount != null && reservationCount > 0) {
            throw new BusinessException("room has reservations and cannot be deleted");
        }
        removeById(id);
    }

    @Override
    public List<RoomAvailabilityVO> listAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new BusinessException("checkOut must be later than checkIn");
        }
        return baseMapper.selectAvailableRooms(checkIn, checkOut);
    }

    @Override
    public PageResult<Room> pageRooms(RoomQueryRequest request) {
        Page<Room> page = new Page<>(request.pageQuery().safePageNo(), request.pageQuery().safePageSize());
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<Room>()
                .like(request.keyword() != null && !request.keyword().isBlank(), Room::getRoomNumber, request.keyword())
                .eq(request.roomTypeId() != null, Room::getRoomTypeId, request.roomTypeId())
                .eq(request.status() != null && !request.status().isBlank(), Room::getStatus, request.status())
                .eq(request.cleanStatus() != null && !request.cleanStatus().isBlank(), Room::getCleanStatus, request.cleanStatus())
                .orderByAsc(Room::getRoomNumber);
        return PageResult.from(page(page, wrapper));
    }

    private void validateRequest(RoomRequest request, Long currentId) {
        roomTypeService.getDetail(request.roomTypeId());
        long duplicateCount = count(new LambdaQueryWrapper<Room>()
                .eq(Room::getRoomNumber, request.roomNumber())
                .ne(currentId != null, Room::getId, currentId));
        if (duplicateCount > 0) {
            throw new BusinessException("room number already exists");
        }
    }

    private void apply(Room room, RoomRequest request) {
        room.setRoomNumber(request.roomNumber());
        room.setRoomTypeId(request.roomTypeId());
        room.setFloor(request.floor());
        room.setStatus(request.status());
        room.setCleanStatus(request.cleanStatus());
    }
}
