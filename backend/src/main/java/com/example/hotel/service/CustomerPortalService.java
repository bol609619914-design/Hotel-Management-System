package com.example.hotel.service;

import com.example.hotel.common.PageResult;
import com.example.hotel.dto.CustomerLoginRequest;
import com.example.hotel.dto.CustomerRegisterRequest;
import com.example.hotel.dto.CustomerReservationRequest;
import com.example.hotel.entity.RoomType;
import com.example.hotel.vo.CustomerProfileVO;
import com.example.hotel.vo.LoginVO;
import com.example.hotel.vo.ReservationVO;
import java.util.List;

public interface CustomerPortalService {

    LoginVO login(CustomerLoginRequest request);

    LoginVO register(CustomerRegisterRequest request);

    CustomerProfileVO currentProfile();

    List<RoomType> listRoomTypes();

    PageResult<ReservationVO> pageMyReservations(Long pageNo, Long pageSize);

    ReservationVO createReservation(CustomerReservationRequest request);
}
