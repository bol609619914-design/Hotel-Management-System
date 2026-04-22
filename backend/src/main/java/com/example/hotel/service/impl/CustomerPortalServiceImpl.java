package com.example.hotel.service.impl;

import com.example.hotel.common.PageResult;
import com.example.hotel.common.SecurityUtil;
import com.example.hotel.dto.CreateReservationRequest;
import com.example.hotel.dto.CustomerLoginRequest;
import com.example.hotel.dto.CustomerRegisterRequest;
import com.example.hotel.dto.CustomerReservationRequest;
import com.example.hotel.dto.PageQuery;
import com.example.hotel.entity.CustomerUser;
import com.example.hotel.entity.Guest;
import com.example.hotel.entity.RoomType;
import com.example.hotel.exception.BusinessException;
import com.example.hotel.service.CustomerPortalService;
import com.example.hotel.service.CustomerUserService;
import com.example.hotel.service.GuestService;
import com.example.hotel.service.ReservationService;
import com.example.hotel.service.RoomTypeService;
import com.example.hotel.vo.CustomerProfileVO;
import com.example.hotel.vo.LoginVO;
import com.example.hotel.vo.ReservationVO;
import com.example.hotel.util.JwtTokenProvider;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerPortalServiceImpl implements CustomerPortalService {

    private final CustomerUserService customerUserService;
    private final ReservationService reservationService;
    private final RoomTypeService roomTypeService;
    private final GuestService guestService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public CustomerPortalServiceImpl(CustomerUserService customerUserService,
                                     ReservationService reservationService,
                                     RoomTypeService roomTypeService,
                                     GuestService guestService,
                                     PasswordEncoder passwordEncoder,
                                     JwtTokenProvider jwtTokenProvider) {
        this.customerUserService = customerUserService;
        this.reservationService = reservationService;
        this.roomTypeService = roomTypeService;
        this.guestService = guestService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginVO login(CustomerLoginRequest request) {
        CustomerUser customerUser = customerUserService.getByPhone(request.phone());
        if (customerUser == null || !"ACTIVE".equals(customerUser.getStatus())) {
            throw new BusinessException("Invalid phone or password");
        }
        if (!passwordEncoder.matches(request.password(), customerUser.getPassword())) {
            throw new BusinessException("Invalid phone or password");
        }
        return buildLoginVO(customerUser);
    }

    @Override
    public LoginVO register(CustomerRegisterRequest request) {
        CustomerUser customerUser = customerUserService.register(request);
        return buildLoginVO(customerUser);
    }

    @Override
    public CustomerProfileVO currentProfile() {
        CustomerUser customerUser = currentCustomer();
        return new CustomerProfileVO(
                customerUser.getId(),
                customerUser.getUsername(),
                customerUser.getPhone(),
                customerUser.getDisplayName(),
                customerUser.getMemberLevel(),
                "CUSTOMER"
        );
    }

    @Override
    public List<RoomType> listRoomTypes() {
        return roomTypeService.listAll();
    }

    @Override
    public PageResult<ReservationVO> pageMyReservations(Long pageNo, Long pageSize) {
        CustomerUser customerUser = currentCustomer();
        Guest guest = guestService.getByPhone(customerUser.getPhone());
        if (guest == null) {
            return new PageResult<>(pageNo == null || pageNo < 1 ? 1 : pageNo,
                    pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100),
                    0,
                    0,
                    List.of());
        }
        return reservationService.pageReservationsByGuestId(guest.getId(), new PageQuery(pageNo, pageSize));
    }

    @Override
    public ReservationVO createReservation(CustomerReservationRequest request) {
        CustomerUser customerUser = currentCustomer();
        return reservationService.createReservation(new CreateReservationRequest(
                customerUser.getDisplayName(),
                customerUser.getPhone(),
                request.idCard(),
                request.roomId(),
                request.checkInDate(),
                request.checkOutDate(),
                request.guestCount(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "DIRECT",
                request.specialRequest()
        ));
    }

    private CustomerUser currentCustomer() {
        String username = SecurityUtil.currentUsername();
        CustomerUser customerUser = customerUserService.getByUsername(username);
        if (customerUser == null) {
            throw new BusinessException("Unauthorized");
        }
        return customerUser;
    }

    private LoginVO buildLoginVO(CustomerUser customerUser) {
        String token = jwtTokenProvider.generateToken(customerUser.getId(), customerUser.getUsername(), "CUSTOMER");
        return new LoginVO(
                customerUser.getId(),
                customerUser.getUsername(),
                customerUser.getDisplayName(),
                "CUSTOMER",
                token
        );
    }
}
