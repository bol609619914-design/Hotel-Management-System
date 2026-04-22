package com.example.hotel.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

public record PageResult<T>(
        long pageNo,
        long pageSize,
        long total,
        long totalPages,
        List<T> records
) {

    public static <T> PageResult<T> from(IPage<T> page) {
        return new PageResult<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getPages(),
                page.getRecords()
        );
    }
}
