package com.example.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hotel.entity.OperationLog;
import com.example.hotel.vo.OperationLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    @Select("""
            <script>
            select
                ol.id,
                ol.reservation_id,
                r.reservation_no,
                rm.room_number,
                ol.operator_username,
                ol.operator_role,
                ol.action_type,
                ol.description,
                ol.before_snapshot,
                ol.after_snapshot,
                ol.created_at
            from operation_log ol
            left join reservation r on r.id = ol.reservation_id
            left join room rm on rm.id = ol.room_id
            <where>
                <if test="query.reservationId != null">
                    and ol.reservation_id = #{query.reservationId}
                </if>
                <if test="query.actionType != null and query.actionType != ''">
                    and ol.action_type = #{query.actionType}
                </if>
                <if test="query.operatorUsername != null and query.operatorUsername != ''">
                    and ol.operator_username like concat('%', #{query.operatorUsername}, '%')
                </if>
            </where>
            order by ol.created_at desc, ol.id desc
            </script>
            """)
    IPage<OperationLogVO> selectLogPage(Page<OperationLogVO> page,
                                        @Param("query") com.example.hotel.dto.OperationLogQueryRequest query);
}
