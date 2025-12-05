package com.library.controller;

import com.library.model.BookCategory;
import com.library.service.BookCategoryService;
import com.library.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

/**
 * 图书分类管理控制器
 */
@RestController
@RequestMapping("/api/book-categories")
@CrossOrigin(origins = "*")
public class BookCategoryController {

    @Autowired
    private BookCategoryService bookCategoryService;

    /**
     * 获取所有分类（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookCategory>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sortOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<BookCategory> categories = bookCategoryService.findAll(pageable);
            return ResponseEntity.ok(ApiResponse.success("获取分类列表成功", categories));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取分类列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有分类（不分页）
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BookCategory>>> getAllCategories() {
        try {
            List<BookCategory> categories = bookCategoryService.findAllActive();
            return ResponseEntity.ok(ApiResponse.success("获取所有分类成功", categories));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取分类列表失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookCategory>> getCategoryById(@PathVariable Long id) {
        try {
            Optional<BookCategory> category = bookCategoryService.findById(id);
            if (category.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("获取分类成功", category.get()));
            } else {
                return ResponseEntity.status(404)
                    .body(ApiResponse.notFound("分类不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取分类失败: " + e.getMessage()));
        }
    }

    /**
     * 创建分类
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookCategory>> createCategory(
            @RequestBody BookCategory category,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            // 验证必填字段
            if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("分类名称不能为空"));
            }

            // 检查分类名称是否已存在
            if (bookCategoryService.existsByCategoryName(category.getCategoryName())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("分类名称已存在"));
            }

            // 检查分类代码是否已存在
            if (category.getCode() != null && bookCategoryService.existsByCode(category.getCode())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("分类代码已存在"));
            }

            BookCategory savedCategory = bookCategoryService.save(category);
            return ResponseEntity.ok(ApiResponse.success("创建分类成功", savedCategory));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("创建分类失败: " + e.getMessage()));
        }
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookCategory>> updateCategory(
            @PathVariable Long id,
            @RequestBody BookCategory category,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            Optional<BookCategory> existingCategoryOpt = bookCategoryService.findById(id);
            if (!existingCategoryOpt.isPresent()) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.notFound("分类不存在"));
            }

            BookCategory existingCategory = existingCategoryOpt.get();

            // 验证必填字段
            if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("分类名称不能为空"));
            }

            // 检查分类名称是否已被其他分类使用
            if (!existingCategory.getCategoryName().equals(category.getCategoryName()) &&
                bookCategoryService.existsByCategoryName(category.getCategoryName())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("分类名称已存在"));
            }

            // 检查分类代码是否已被其他分类使用
            if (category.getCode() != null && !category.getCode().equals(existingCategory.getCode()) &&
                bookCategoryService.existsByCode(category.getCode())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("分类代码已存在"));
            }

            // 更新字段
            existingCategory.setCategoryName(category.getCategoryName());
            existingCategory.setCode(category.getCode());
            existingCategory.setDescription(category.getDescription());
            existingCategory.setParentId(category.getParentId());
            existingCategory.setSortOrder(category.getSortOrder());
            existingCategory.setStatus(category.getStatus());

            BookCategory updatedCategory = bookCategoryService.save(existingCategory);
            return ResponseEntity.ok(ApiResponse.success("更新分类成功", updatedCategory));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("更新分类失败: " + e.getMessage()));
        }
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(
            @PathVariable Long id,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            Optional<BookCategory> categoryOpt = bookCategoryService.findById(id);
            if (!categoryOpt.isPresent()) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.notFound("分类不存在"));
            }

            // 检查是否有图书使用该分类
            if (bookCategoryService.hasBooks(id)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("该分类下还有图书，无法删除"));
            }

            bookCategoryService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("删除分类成功", "分类已删除"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("删除分类失败: " + e.getMessage()));
        }
    }

    /**
     * 搜索分类
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BookCategory>>> searchCategories(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BookCategory> categories = bookCategoryService.searchCategories(keyword, pageable);
            return ResponseEntity.ok(ApiResponse.success("搜索分类成功", categories));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("搜索分类失败: " + e.getMessage()));
        }
    }

    /**
     * 获取分类统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getCategoryStats() {
        try {
            Object stats = bookCategoryService.getCategoryStats();
            return ResponseEntity.ok(ApiResponse.success("获取分类统计成功", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取分类统计失败: " + e.getMessage()));
        }
    }
}