package com.example.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hotel.entity.FinancialTransaction;
import com.example.hotel.vo.FinanceSummaryVO;
import com.example.hotel.vo.FinanceTransactionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FinancialTransactionMapper extends BaseMapper<FinancialTransaction> {

    @Select("""
            <script>
            select
                ft.id,
                ft.reservation_id,
                ft.reservation_no,
                g.full_name as guest_name,
                rm.room_number,
                ft.transaction_type,
                ft.amount,
                ft.direction,
                ft.remark,
                ft.created_at
            from financial_transaction ft
            left join reservation r on r.id = ft.reservation_id
            left join guest g on g.id = r.guest_id
            left join room rm on rm.id = r.room_id
            <where>
                <if test="query.reservationId != null">
                    and ft.reservation_id = #{query.reservationId}
                </if>
                <if test="query.transactionType != null and query.transactionType != ''">
                    and ft.transaction_type = #{query.transactionType}
                </if>
                <if test="query.direction != null and query.direction != ''">
                    and ft.direction = #{query.direction}
                </if>
            </where>
            order by ft.created_at desc, ft.id desc
            </script>
            """)
    IPage<FinanceTransactionVO> selectTransactionPage(Page<FinanceTransactionVO> page,
                                                      @Param("query") com.example.hotel.dto.FinanceQueryRequest query);

    @Select("""
            <script>
            select
                count(*) as transaction_count,
                coalesce(sum(case when direction = 'CHARGE' then amount else 0 end), 0) as charge_total,
                coalesce(sum(case when direction = 'DISCOUNT' then amount else 0 end), 0) as discount_total,
                coalesce(sum(case when direction = 'REFUND' then amount else 0 end), 0) as refund_total,
                coalesce(sum(case
                    when direction = 'CHARGE' then amount
                    when direction = 'DISCOUNT' then -amount
                    when direction = 'REFUND' then -amount
                    else 0 end), 0) as net_total
            from financial_transaction ft
            <where>
                <if test="query.reservationId != null">
                    and ft.reservation_id = #{query.reservationId}
                </if>
                <if test="query.transactionType != null and query.transactionType != ''">
                    and ft.transaction_type = #{query.transactionType}
                </if>
                <if test="query.direction != null and query.direction != ''">
                    and ft.direction = #{query.direction}
                </if>
            </where>
            </script>
            """)
    FinanceSummaryVO selectTransactionSummary(@Param("query") com.example.hotel.dto.FinanceQueryRequest query);
}
