package com.example.hotel.controller;

import com.example.hotel.common.ApiResponse;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.GuestQueryRequest;
import com.example.hotel.dto.GuestRequest;
import com.example.hotel.entity.Guest;
import com.example.hotel.service.GuestService;
import com.example.hotel.vo.GuestProfileVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guests")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public ApiResponse<PageResult<Guest>> page(GuestQueryRequest request) {
        return ApiResponse.success(guestService.pageGuests(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<Guest> detail(@PathVariable Long id) {
        return ApiResponse.success(guestService.getDetail(id));
    }

    @GetMapping("/{id}/profile")
    public ApiResponse<GuestProfileVO> profile(@PathVariable Long id) {
        return ApiResponse.success(guestService.getProfile(id));
    }

    @PostMapping
    public ApiResponse<Guest> create(@Valid @RequestBody GuestRequest request) {
        return ApiResponse.success("guest created", guestService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Guest> update(@PathVariable Long id, @Valid @RequestBody GuestRequest request) {
        return ApiResponse.success("guest updated", guestService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        guestService.delete(id);
        return ApiResponse.success("guest deleted", null);
    }
}
