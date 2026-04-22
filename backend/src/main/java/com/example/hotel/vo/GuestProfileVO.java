package com.example.hotel.vo;

import com.example.hotel.entity.Guest;
import java.util.List;

public record GuestProfileVO(
        Guest guest,
        GuestStatsVO stats,
        List<GuestStayHistoryVO> history
) {
}
