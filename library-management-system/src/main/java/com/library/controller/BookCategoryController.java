package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.library.common.Result;
import com.library.entity.BookCategory;
import com.library.service.BookCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class BookCategoryController {

    @Resource
    private BookCategoryService bookCategoryService;

    @GetMapping("/list")
    public Result<List<BookCategory>> getCategoryList() {
        List<BookCategory> categories = bookCategoryService.getActiveCategories();
        return Result.success(categories);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<BookCategory>> getAllCategories() {
        QueryWrapper<BookCategory> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort_order");
        List<BookCategory> categories = bookCategoryService.list(wrapper);
        return Result.success(categories);
    }

    @GetMapping("/{id}")
    public Result<BookCategory> getCategoryDetail(@PathVariable Long id) {
        BookCategory category = bookCategoryService.getById(id);
        return category != null ? Result.success(category) : Result.error("分类不存在");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> addCategory(@RequestBody BookCategory category) {
        // 检查分类编码是否已存在
        if (bookCategoryService.checkCategoryExists(category.getCategoryCode())) {
            return Result.error("分类编码已存在");
        }

        category.setStatus(1);
        return bookCategoryService.save(category) ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateCategory(@PathVariable Long id, @RequestBody BookCategory category) {
        BookCategory existingCategory = bookCategoryService.getById(id);
        if (existingCategory == null) {
            return Result.error("分类不存在");
        }

        // 如果修改了分类编码，检查是否与其他分类冲突
        if (!existingCategory.getCategoryCode().equals(category.getCategoryCode())) {
            if (bookCategoryService.checkCategoryExists(category.getCategoryCode())) {
                return Result.error("分类编码已存在");
            }
        }

        category.setId(id);
        return bookCategoryService.updateById(category) ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteCategory(@PathVariable Long id) {
        return bookCategoryService.removeById(id) ? Result.success("删除成功") : Result.error("删除失败");
    }
}