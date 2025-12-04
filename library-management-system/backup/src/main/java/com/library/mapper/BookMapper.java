package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    Page<Book> selectBooksWithCategory(Page<Book> page, @Param("title") String title,
                                      @Param("author") String author, @Param("categoryId") Long categoryId);
}