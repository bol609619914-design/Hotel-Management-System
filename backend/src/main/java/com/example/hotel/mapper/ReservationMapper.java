package com.example.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hotel.entity.Reservation;
import com.example.hotel.vo.ReservationVO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    @Select("""
            select count(*)
            from reservation
            where status in ('BOOKED', 'CHECKED_IN')
              and check_in_date <= curdate()
              and check_out_date > curdate()
            """)
    Long countInHouseReservations();

    @Select("""
            select count(*)
            from reservation
            where status in ('BOOKED', 'CHECKED_IN')
              and check_in_date = curdate()
            """)
    Long countTodayArrivals();

    @Select("""
            select coalesce(sum(total_amount), 0)
            from reservation
            where status in ('BOOKED', 'CHECKED_IN')
              and check_in_date >= date_sub(curdate(), interval 30 day)
            """)
    BigDecimal sumLastThirtyDaysRevenue();

    @Select("""
            select
                r.id,
                r.reservation_no,
                r.guest_id,
                g.full_name as guest_name,
                g.phone,
                rm.room_number,
                r.check_in_date,
                r.check_out_date,
                r.guest_count,
                r.room_fee,
                r.breakfast_fee,
                r.extra_bed_fee,
                r.deposit_amount,
                r.coupon_amount,
                r.total_amount,
                r.status,
                r.channel,
                r.special_request,
                r.created_at,
                r.actual_check_in_time,
                r.actual_check_out_time
            from reservation r
            join guest g on g.id = r.guest_id
            join room rm on rm.id = r.room_id
            order by r.created_at desc
            """)
    List<ReservationVO> selectReservationDetails();

    @Select("""
            select
                r.id,
                r.reservation_no,
                r.guest_id,
                g.full_name as guest_name,
                g.phone,
                rm.room_number,
                r.check_in_date,
                r.check_out_date,
                r.guest_count,
                r.room_fee,
                r.breakfast_fee,
                r.extra_bed_fee,
                r.deposit_amount,
                r.coupon_amount,
                r.total_amount,
                r.status,
                r.channel,
                r.special_request,
                r.created_at,
                r.actual_check_in_time,
                r.actual_check_out_time
            from reservation r
            join guest g on g.id = r.guest_id
            join room rm on rm.id = r.room_id
            where r.id = #{id}
            limit 1
            """)
    ReservationVO selectReservationDetailById(@Param("id") Long id);

    @Select("""
            select count(*)
            from reservation
            where room_id = #{roomId}
              and status in ('BOOKED', 'CHECKED_IN')
              and check_in_date < #{checkOut}
              and check_out_date > #{checkIn}
              and (#{excludeId} is null or id <> #{excludeId})
            """)
    Long countReservationConflicts(@Param("roomId") Long roomId,
                                   @Param("checkIn") LocalDate checkIn,
                                   @Param("checkOut") LocalDate checkOut,
                                   @Param("excludeId") Long excludeId);

    @Select("""
            select count(*)
            from reservation
            where room_id = #{roomId}
            """)
    Long countByRoomId(@Param("roomId") Long roomId);

    @Select("""
            <script>
            select
                r.id,
                r.reservation_no,
                r.guest_id,
                g.full_name as guest_name,
                g.phone,
                rm.room_number,
                r.check_in_date,
                r.check_out_date,
                r.guest_count,
                r.room_fee,
                r.breakfast_fee,
                r.extra_bed_fee,
                r.deposit_amount,
                r.coupon_amount,
                r.total_amount,
                r.status,
                r.channel,
                r.special_request,
                r.created_at,
                r.actual_check_in_time,
                r.actual_check_out_time
            from reservation r
            join guest g on g.id = r.guest_id
            join room rm on rm.id = r.room_id
            <where>
                <if test="query.keyword != null and query.keyword != ''">
                    and (
                        r.reservation_no like concat('%', #{query.keyword}, '%')
                        or g.full_name like concat('%', #{query.keyword}, '%')
                        or g.phone like concat('%', #{query.keyword}, '%')
                    )
                </if>
                <if test="query.status != null and query.status != ''">
                    and r.status = #{query.status}
                </if>
                <if test="query.channel != null and query.channel != ''">
                    and r.channel = #{query.channel}
                </if>
                <if test="query.roomNumber != null and query.roomNumber != ''">
                    and rm.room_number like concat('%', #{query.roomNumber}, '%')
                </if>
                <if test="query.checkInFrom != null">
                    and r.check_in_date <![CDATA[ >= ]]> #{query.checkInFrom}
                </if>
                <if test="query.checkInTo != null">
                    and r.check_in_date <![CDATA[ <= ]]> #{query.checkInTo}
                </if>
            </where>
            order by r.created_at desc
            </script>
            """)
    IPage<ReservationVO> selectReservationPage(Page<ReservationVO> page,
                                               @Param("query") com.example.hotel.dto.ReservationQueryRequest query);

    @Select("""
            select
                r.id,
                r.reservation_no,
                r.guest_id,
                g.full_name as guest_name,
                g.phone,
                rm.room_number,
                r.check_in_date,
                r.check_out_date,
                r.guest_count,
                r.room_fee,
                r.breakfast_fee,
                r.extra_bed_fee,
                r.deposit_amount,
                r.coupon_amount,
                r.total_amount,
                r.status,
                r.channel,
                r.special_request,
                r.created_at,
                r.actual_check_in_time,
                r.actual_check_out_time
            from reservation r
            join guest g on g.id = r.guest_id
            join room rm on rm.id = r.room_id
            where r.guest_id = #{guestId}
            order by r.created_at desc
            """)
    IPage<ReservationVO> selectReservationPageByGuestId(Page<ReservationVO> page, @Param("guestId") Long guestId);

    @Select("""
            select coalesce(sum(total_amount), 0)
            from reservation
            where guest_id = #{guestId}
              and status = 'CHECKED_OUT'
            """)
    BigDecimal sumGuestSpent(@Param("guestId") Long guestId);

    @Select("""
            select count(*)
            from reservation
            where guest_id = #{guestId}
              and status = 'CHECKED_OUT'
            """)
    Long countGuestCompletedStays(@Param("guestId") Long guestId);

    @Select("""
            select count(*)
            from reservation
            where guest_id = #{guestId}
            """)
    Long countGuestReservations(@Param("guestId") Long guestId);

    @Select("""
            select count(*)
            from reservation
            where guest_id = #{guestId}
              and status = 'BOOKED'
            """)
    Long countGuestBookedStays(@Param("guestId") Long guestId);

    @Select("""
            select max(check_in_date)
            from reservation
            where guest_id = #{guestId}
            """)
    java.time.LocalDate findGuestLastCheckInDate(@Param("guestId") Long guestId);

    @Select("""
            select
                r.id as reservation_id,
                r.reservation_no,
                rm.room_number,
                r.check_in_date,
                r.check_out_date,
                r.total_amount,
                r.status,
                r.channel,
                r.actual_check_in_time,
                r.actual_check_out_time
            from reservation r
            join room rm on rm.id = r.room_id
            where r.guest_id = #{guestId}
            order by r.check_in_date desc, r.created_at desc
            """)
    java.util.List<com.example.hotel.vo.GuestStayHistoryVO> selectGuestHistory(@Param("guestId") Long guestId);

    @Select("""
            select
                d.report_date as date,
                coalesce(revenue.total_revenue, 0) as revenue,
                coalesce(arrivals.arrivals, 0) as arrivals,
                coalesce(departures.departures, 0) as departures,
                coalesce(booked.booked_count, 0) as booked_count
            from (
                select curdate() - interval 6 day as report_date
                union all select curdate() - interval 5 day
                union all select curdate() - interval 4 day
                union all select curdate() - interval 3 day
                union all select curdate() - interval 2 day
                union all select curdate() - interval 1 day
                union all select curdate()
            ) d
            left join (
                select check_in_date, sum(total_amount) as total_revenue
                from reservation
                where status in ('BOOKED', 'CHECKED_IN', 'CHECKED_OUT')
                  and check_in_date between curdate() - interval 6 day and curdate()
                group by check_in_date
            ) revenue on revenue.check_in_date = d.report_date
            left join (
                select check_in_date, count(*) as arrivals
                from reservation
                where check_in_date between curdate() - interval 6 day and curdate()
                group by check_in_date
            ) arrivals on arrivals.check_in_date = d.report_date
            left join (
                select check_out_date, count(*) as departures
                from reservation
                where check_out_date between curdate() - interval 6 day and curdate()
                group by check_out_date
            ) departures on departures.check_out_date = d.report_date
            left join (
                select check_in_date, count(*) as booked_count
                from reservation
                where status in ('BOOKED', 'CHECKED_IN', 'CHECKED_OUT')
                  and check_in_date between curdate() - interval 6 day and curdate()
                group by check_in_date
            ) booked on booked.check_in_date = d.report_date
            order by d.report_date
            """)
    java.util.List<com.example.hotel.vo.DashboardTrendPointVO> selectRecentTrendPoints();
}
