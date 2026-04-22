package com.example.hotel.controller;

import com.example.hotel.common.ApiResponse;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.RoomTypeQueryRequest;
import com.example.hotel.dto.RoomTypeRequest;
import com.example.hotel.entity.RoomType;
import com.example.hotel.service.RoomTypeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/room-types")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @GetMapping
    public ApiResponse<List<RoomType>> list() {
        return ApiResponse.success(roomTypeService.listAll());
    }

    @GetMapping("/page")
    public ApiResponse<PageResult<RoomType>> page(RoomTypeQueryRequest request) {
        return ApiResponse.success(roomTypeService.pageRoomTypes(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<RoomType> detail(@PathVariable Long id) {
        return ApiResponse.success(roomTypeService.getDetail(id));
    }

    @PostMapping
    public ApiResponse<RoomType> create(@Valid @RequestBody RoomTypeRequest request) {
        return ApiResponse.success("room type created", roomTypeService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<RoomType> update(@PathVariable Long id, @Valid @RequestBody RoomTypeRequest request) {
        return ApiResponse.success("room type updated", roomTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roomTypeService.delete(id);
        return ApiResponse.success("room type deleted", null);
    }
}
