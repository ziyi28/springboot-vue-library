package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {

    Page<BorrowRecord> selectBorrowRecordsWithDetails(Page<BorrowRecord> page,
                                                    @Param("userId") Long userId,
                                                    @Param("status") Integer status);
}