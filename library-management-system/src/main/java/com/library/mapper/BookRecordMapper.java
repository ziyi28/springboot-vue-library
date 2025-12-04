package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BookRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BookRecordMapper extends BaseMapper<BookRecord> {

    @Update("UPDATE book_records SET status = #{status} WHERE book_id = #{bookId} AND status = 1 LIMIT 1")
    int updateAvailableBookRecordStatus(@Param("bookId") Long bookId, @Param("status") Integer status);

    @Update("UPDATE book_records SET status = 1 WHERE id = #{id}")
    int returnBookRecord(@Param("id") Long id);
}