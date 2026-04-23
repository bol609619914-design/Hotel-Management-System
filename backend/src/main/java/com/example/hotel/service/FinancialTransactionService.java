package com.example.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.FinanceQueryRequest;
import com.example.hotel.entity.FinancialTransaction;
import com.example.hotel.vo.FinanceSummaryVO;
import com.example.hotel.entity.Reservation;
import com.example.hotel.vo.FinanceTransactionVO;

public interface FinancialTransactionService extends IService<FinancialTransaction> {

    void recordReservationCreated(Reservation reservation);

    void recordReservationAdjustment(Reservation before, Reservation after, String reason);

    void recordStatusEvent(Reservation reservation, String transactionType, String remark);

    PageResult<FinanceTransactionVO> pageTransactions(FinanceQueryRequest request);

    FinanceSummaryVO getSummary(FinanceQueryRequest request);
}
