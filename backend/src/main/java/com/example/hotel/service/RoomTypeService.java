package com.example.hotel.service;

import com.example.hotel.common.PageResult;
import com.example.hotel.dto.RoomTypeQueryRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hotel.dto.RoomTypeRequest;
import com.example.hotel.entity.RoomType;
import java.util.List;

public interface RoomTypeService extends IService<RoomType> {

    List<RoomType> listAll();

    RoomType getDetail(Long id);

    RoomType create(RoomTypeRequest request);

    RoomType update(Long id, RoomTypeRequest request);

    void delete(Long id);

    PageResult<RoomType> pageRoomTypes(RoomTypeQueryRequest request);
}
