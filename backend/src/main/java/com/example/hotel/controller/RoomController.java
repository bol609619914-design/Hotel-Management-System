package com.example.hotel.controller;

import com.example.hotel.common.ApiResponse;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.RoomQueryRequest;
import com.example.hotel.dto.RoomRequest;
import com.example.hotel.entity.Room;
import com.example.hotel.service.RoomService;
import com.example.hotel.vo.RoomAvailabilityVO;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ApiResponse<List<Room>> list() {
        return ApiResponse.success(roomService.listAllRooms());
    }

    @GetMapping("/page")
    public ApiResponse<PageResult<Room>> page(RoomQueryRequest request) {
        return ApiResponse.success(roomService.pageRooms(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<Room> detail(@PathVariable Long id) {
        return ApiResponse.success(roomService.getDetail(id));
    }

    @PostMapping
    public ApiResponse<Room> create(@Valid @RequestBody RoomRequest request) {
        return ApiResponse.success("room created", roomService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Room> update(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return ApiResponse.success("room updated", roomService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ApiResponse.success("room deleted", null);
    }

    @GetMapping("/available")
    public ApiResponse<List<RoomAvailabilityVO>> availableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return ApiResponse.success(roomService.listAvailableRooms(checkIn, checkOut));
    }
}
