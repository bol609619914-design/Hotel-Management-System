package com.example.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hotel.entity.Room;
import com.example.hotel.vo.RoomAvailabilityVO;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {

    @Select("""
            select count(*)
            from room
            where status = 'AVAILABLE'
            """)
    Long countAvailableRooms();

    @Select("""
            select count(*)
            from room
            where room_type_id = #{roomTypeId}
            """)
    Long countByRoomTypeId(Long roomTypeId);

    @Select("""
            select
                r.id,
                r.room_number,
                r.floor,
                r.clean_status,
                rt.name as room_type_name,
                rt.base_price,
                rt.max_guests,
                rt.bed_type,
                rt.area,
                rt.description,
                rt.amenities
            from room r
            join room_type rt on rt.id = r.room_type_id
            where r.status = 'AVAILABLE'
              and not exists (
                select 1
                from reservation rs
                where rs.room_id = r.id
                  and rs.status in ('BOOKED', 'CHECKED_IN')
                  and rs.check_in_date < #{checkOut}
                  and rs.check_out_date > #{checkIn}
              )
            order by r.room_number
            """)
    List<RoomAvailabilityVO> selectAvailableRooms(@Param("checkIn") LocalDate checkIn,
                                                  @Param("checkOut") LocalDate checkOut);
}
