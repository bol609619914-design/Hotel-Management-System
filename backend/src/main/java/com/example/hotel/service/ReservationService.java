package com.example.hotel.service;

import com.example.hotel.common.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hotel.dto.CreateReservationRequest;
import com.example.hotel.dto.ReservationQueryRequest;
import com.example.hotel.dto.ReservationExtendRequest;
import com.example.hotel.dto.ReservationRoomChangeRequest;
import com.example.hotel.dto.ReservationStatusRequest;
import com.example.hotel.dto.PageQuery;
import com.example.hotel.entity.Reservation;
import com.example.hotel.vo.ReservationPrintVO;
import com.example.hotel.vo.ReservationVO;
import java.util.List;

public interface ReservationService extends IService<Reservation> {

    ReservationVO createReservation(CreateReservationRequest request);

    ReservationVO updateReservation(Long id, CreateReservationRequest request);

    ReservationVO getReservationDetail(Long id);

    void deleteReservation(Long id);

    List<ReservationVO> listReservationDetails();

    PageResult<ReservationVO> pageReservations(ReservationQueryRequest request);

    PageResult<ReservationVO> pageReservationsByGuestId(Long guestId, PageQuery pageQuery);

    ReservationVO updateReservationStatus(Long id, ReservationStatusRequest request);

    ReservationVO extendReservation(Long id, ReservationExtendRequest request);

    ReservationVO changeReservationRoom(Long id, ReservationRoomChangeRequest request);

    ReservationPrintVO getReservationPrint(Long id);

    ReservationPrintVO getCheckInPrint(Long id);

    ReservationPrintVO getCheckoutSettlement(Long id);
}
