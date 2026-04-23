package com.example.hotel.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.hotel.common.PageResult;
import com.example.hotel.dto.FinanceQueryRequest;
import com.example.hotel.entity.FinancialTransaction;
import com.example.hotel.entity.Reservation;
import com.example.hotel.mapper.FinancialTransactionMapper;
import com.example.hotel.service.FinancialTransactionService;
import com.example.hotel.vo.FinanceSummaryVO;
import com.example.hotel.vo.FinanceTransactionVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class FinancialTransactionServiceImpl extends ServiceImpl<FinancialTransactionMapper, FinancialTransaction>
        implements FinancialTransactionService {

    @Override
    public void recordReservationCreated(Reservation reservation) {
        insertCharge(reservation, "ROOM_FEE", reservation.getRoomFee(), "CHARGE", "预订房费入账");
        insertCharge(reservation, "BREAKFAST_FEE", reservation.getBreakfastFee(), "CHARGE", "预订早餐费用");
        insertCharge(reservation, "EXTRA_BED_FEE", reservation.getExtraBedFee(), "CHARGE", "预订加床费用");
        insertCharge(reservation, "DEPOSIT", reservation.getDepositAmount(), "CHARGE", "预订押金");
        insertCharge(reservation, "COUPON", reservation.getCouponAmount(), "DISCOUNT", "预订优惠券抵扣");
    }

    @Override
    public void recordReservationAdjustment(Reservation before, Reservation after, String reason) {
        recordDelta(after, "ROOM_FEE_ADJUSTMENT", before.getRoomFee(), after.getRoomFee(), reason + " - 房费调整");
        recordDelta(after, "BREAKFAST_ADJUSTMENT", before.getBreakfastFee(), after.getBreakfastFee(), reason + " - 早餐调整");
        recordDelta(after, "EXTRA_BED_ADJUSTMENT", before.getExtraBedFee(), after.getExtraBedFee(), reason + " - 加床调整");
        recordDelta(after, "DEPOSIT_ADJUSTMENT", before.getDepositAmount(), after.getDepositAmount(), reason + " - 押金调整");
        recordDelta(after, "COUPON_ADJUSTMENT", before.getCouponAmount(), after.getCouponAmount(), reason + " - 优惠调整", true);
    }

    @Override
    public void recordStatusEvent(Reservation reservation, String transactionType, String remark) {
        insertCharge(reservation, transactionType, reservation.getTotalAmount(), "CHARGE", remark);
    }

    @Override
    public PageResult<FinanceTransactionVO> pageTransactions(FinanceQueryRequest request) {
        Page<FinanceTransactionVO> page = new Page<>(request.pageQuery().safePageNo(), request.pageQuery().safePageSize());
        return PageResult.from(baseMapper.selectTransactionPage(page, request));
    }

    @Override
    public FinanceSummaryVO getSummary(FinanceQueryRequest request) {
        FinanceSummaryVO summary = baseMapper.selectTransactionSummary(request);
        if (summary == null) {
            return new FinanceSummaryVO(0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return summary;
    }

    private void recordDelta(Reservation reservation,
                             String transactionType,
                             BigDecimal before,
                             BigDecimal after,
                             String remark) {
        recordDelta(reservation, transactionType, before, after, remark, false);
    }

    private void recordDelta(Reservation reservation,
                             String transactionType,
                             BigDecimal before,
                             BigDecimal after,
                             String remark,
                             boolean discountMode) {
        BigDecimal beforeValue = safe(before);
        BigDecimal afterValue = safe(after);
        BigDecimal diff = afterValue.subtract(beforeValue);
        if (diff.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        String direction;
        if (discountMode) {
            direction = diff.signum() > 0 ? "DISCOUNT" : "CHARGE";
        } else {
            direction = diff.signum() > 0 ? "CHARGE" : "REFUND";
        }
        insertCharge(reservation, transactionType, diff.abs(), direction, remark);
    }

    private void insertCharge(Reservation reservation,
                              String transactionType,
                              BigDecimal amount,
                              String direction,
                              String remark) {
        BigDecimal safeAmount = safe(amount);
        if (safeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setReservationId(reservation.getId());
        transaction.setReservationNo(reservation.getReservationNo());
        transaction.setTransactionType(transactionType);
        transaction.setAmount(safeAmount);
        transaction.setDirection(direction);
        transaction.setRemark(remark);
        transaction.setCreatedAt(LocalDateTime.now());
        save(transaction);
    }

    private BigDecimal safe(BigDecimal value) {
        return Objects.requireNonNullElse(value, BigDecimal.ZERO);
    }
}
