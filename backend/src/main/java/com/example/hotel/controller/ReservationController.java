package com.example.hotel.controller;

import com.example.hotel.common.ApiResponse;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.CreateReservationRequest;
import com.example.hotel.dto.ReservationQueryRequest;
import com.example.hotel.dto.ReservationStatusRequest;
import com.example.hotel.service.ReservationService;
import com.example.hotel.vo.ReservationPrintVO;
import com.example.hotel.vo.ReservationVO;
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
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ApiResponse<List<ReservationVO>> list() {
        return ApiResponse.success(reservationService.listReservationDetails());
    }

    @GetMapping("/page")
    public ApiResponse<PageResult<ReservationVO>> page(ReservationQueryRequest request) {
        return ApiResponse.success(reservationService.pageReservations(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ReservationVO> detail(@PathVariable Long id) {
        return ApiResponse.success(reservationService.getReservationDetail(id));
    }

    @GetMapping("/{id}/print")
    public ApiResponse<ReservationPrintVO> print(@PathVariable Long id) {
        return ApiResponse.success(reservationService.getReservationPrint(id));
    }

    @GetMapping("/{id}/check-in-slip")
    public ApiResponse<ReservationPrintVO> checkInSlip(@PathVariable Long id) {
        return ApiResponse.success(reservationService.getCheckInPrint(id));
    }

    @GetMapping("/{id}/checkout-settlement")
    public ApiResponse<ReservationPrintVO> checkoutSettlement(@PathVariable Long id) {
        return ApiResponse.success(reservationService.getCheckoutSettlement(id));
    }

    @PostMapping
    public ApiResponse<ReservationVO> create(@Valid @RequestBody CreateReservationRequest request) {
        return ApiResponse.success("reservation created", reservationService.createReservation(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ReservationVO> update(@PathVariable Long id, @Valid @RequestBody CreateReservationRequest request) {
        return ApiResponse.success("reservation updated", reservationService.updateReservation(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ApiResponse.success("reservation deleted", null);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<ReservationVO> updateStatus(@PathVariable Long id,
                                                   @Valid @RequestBody ReservationStatusRequest request) {
        return ApiResponse.success("reservation status updated", reservationService.updateReservationStatus(id, request));
    }
}
