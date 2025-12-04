package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BookCategory;
import com.library.mapper.BookCategoryMapper;
import com.library.service.BookCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCategoryServiceImpl extends ServiceImpl<BookCategoryMapper, BookCategory> implements BookCategoryService {

    @Override
    public List<BookCategory> getActiveCategories() {
        QueryWrapper<BookCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByAsc("sort_order");
        return this.list(wrapper);
    }

    @Override
    public boolean checkCategoryExists(String categoryCode) {
        QueryWrapper<BookCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("category_code", categoryCode);
        wrapper.eq("is_deleted", 0);
        return this.count(wrapper) > 0;
    }
}