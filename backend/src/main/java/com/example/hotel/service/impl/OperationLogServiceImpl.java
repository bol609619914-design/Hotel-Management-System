package com.example.hotel.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.hotel.common.PageResult;
import com.example.hotel.common.SecurityUtil;
import com.example.hotel.dto.OperationLogQueryRequest;
import com.example.hotel.entity.OperationLog;
import com.example.hotel.entity.Reservation;
import com.example.hotel.mapper.OperationLogMapper;
import com.example.hotel.service.OperationLogService;
import com.example.hotel.vo.OperationLogVO;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public void logReservationAction(String actionType,
                                     Reservation reservation,
                                     Long roomId,
                                     String description,
                                     String beforeSnapshot,
                                     String afterSnapshot) {
        OperationLog log = new OperationLog();
        log.setReservationId(reservation == null ? null : reservation.getId());
        log.setRoomId(roomId);
        log.setOperatorUsername(SecurityUtil.currentUsername());
        log.setOperatorRole(SecurityUtil.currentRole());
        log.setActionType(actionType);
        log.setDescription(description);
        log.setBeforeSnapshot(beforeSnapshot);
        log.setAfterSnapshot(afterSnapshot);
        log.setCreatedAt(LocalDateTime.now());
        save(log);
    }

    @Override
    public PageResult<OperationLogVO> pageLogs(OperationLogQueryRequest request) {
        Page<OperationLogVO> page = new Page<>(request.pageQuery().safePageNo(), request.pageQuery().safePageSize());
        return PageResult.from(baseMapper.selectLogPage(page, request));
    }
}
