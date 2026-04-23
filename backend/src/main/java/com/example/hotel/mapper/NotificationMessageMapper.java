package com.example.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hotel.entity.NotificationMessage;
import com.example.hotel.vo.NotificationVO;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface NotificationMessageMapper extends BaseMapper<NotificationMessage> {

    @Select("""
            <script>
            select
                id,
                category,
                title,
                content,
                related_type,
                related_id,
                target_role,
                status,
                scheduled_at,
                created_at,
                read_at
            from notification_message
            <where>
                and target_role in ('STAFF', #{role})
                <if test="query.status != null and query.status != ''">
                    and status = #{query.status}
                </if>
                <if test="query.category != null and query.category != ''">
                    and category = #{query.category}
                </if>
            </where>
            order by
                case when status = 'UNREAD' then 0 else 1 end,
                scheduled_at desc,
                created_at desc,
                id desc
            </script>
            """)
    IPage<NotificationVO> selectNotificationPage(Page<NotificationVO> page,
                                                 @Param("query") com.example.hotel.dto.NotificationQueryRequest query,
                                                 @Param("role") String role);

    @Select("""
            select count(*)
            from notification_message
            where category = #{category}
              and related_id = #{relatedId}
              and target_role = #{targetRole}
              and scheduled_at = #{scheduledAt}
            """)
    Long countDuplicate(@Param("category") String category,
                        @Param("relatedId") Long relatedId,
                        @Param("targetRole") String targetRole,
                        @Param("scheduledAt") LocalDateTime scheduledAt);
}
