package com.example.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.OperationLogQueryRequest;
import com.example.hotel.entity.OperationLog;
import com.example.hotel.entity.Reservation;
import com.example.hotel.vo.OperationLogVO;

public interface OperationLogService extends IService<OperationLog> {

    void logReservationAction(String actionType,
                              Reservation reservation,
                              Long roomId,
                              String description,
                              String beforeSnapshot,
                              String afterSnapshot);

    PageResult<OperationLogVO> pageLogs(OperationLogQueryRequest request);
}
