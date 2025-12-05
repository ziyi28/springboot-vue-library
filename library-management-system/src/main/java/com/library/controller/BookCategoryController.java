package com.library.controller;

import com.library.model.BookCategory;
import com.library.service.BookCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 图书分类控制器
 * 管理图书分类的增删改查功能
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookCategoryController {

    @Autowired
    private BookCategoryService bookCategoryService;

    /**
     * 获取所有分类
     */
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        List<BookCategory> categories = bookCategoryService.getAllCategories();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categories);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        Optional<BookCategory> category = bookCategoryService.getCategoryById(id);
        if (category.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", category.get());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "分类不存在");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 创建分类
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> createCategory(@Valid @RequestBody BookCategory category) {
        try {
            BookCategory createdCategory = bookCategoryService.createCategory(category);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类创建成功");
            response.put("data", createdCategory);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody BookCategory category) {
        try {
            BookCategory updatedCategory = bookCategoryService.updateCategory(id, category);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类更新成功");
            response.put("data", updatedCategory);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            bookCategoryService.deleteCategory(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 启用分类
     */
    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> enableCategory(@PathVariable Long id) {
        bookCategoryService.enableCategory(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "分类已启用");
        return ResponseEntity.ok(response);
    }

    /**
     * 禁用分类
     */
    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> disableCategory(@PathVariable Long id) {
        bookCategoryService.disableCategory(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "分类已禁用");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取启用的分类
     */
    @GetMapping("/enabled")
    public ResponseEntity<?> getEnabledCategories() {
        List<BookCategory> categories = bookCategoryService.getEnabledCategories();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categories);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据父级ID获取子分类
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<?> getCategoriesByParentId(@PathVariable Long parentId) {
        List<BookCategory> categories = bookCategoryService.getCategoriesByParentId(parentId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categories);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取顶级分类（父级ID为null）
     */
    @GetMapping("/top-level")
    public ResponseEntity<?> getTopLevelCategories() {
        List<BookCategory> categories = bookCategoryService.getTopLevelCategories();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categories);
        return ResponseEntity.ok(response);
    }

    /**
     * 搜索分类
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchCategories(@RequestParam String keyword) {
        List<BookCategory> categories = bookCategoryService.searchCategories(keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categories);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取分类统计信息
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> getCategoryStats() {
        long totalCategories = bookCategoryService.getTotalCategoryCount();
        long enabledCategories = bookCategoryService.getEnabledCategoryCount();
        long topLevelCategories = bookCategoryService.getTopLevelCategoryCount();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCategories", totalCategories);
        stats.put("enabledCategories", enabledCategories);
        stats.put("disabledCategories", totalCategories - enabledCategories);
        stats.put("topLevelCategories", topLevelCategories);
        stats.put("subCategories", totalCategories - topLevelCategories);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);

        return ResponseEntity.ok(response);
    }

    /**
     * 重新排序分类
     */
    @PutMapping("/reorder")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> reorderCategories(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> categoryOrders = (List<Map<String, Object>>) request.get("categories");

        for (Map<String, Object> categoryOrder : categoryOrders) {
            Long id = Long.valueOf(categoryOrder.get("id").toString());
            Integer sortOrder = (Integer) categoryOrder.get("sortOrder");
            bookCategoryService.updateCategorySort(id, sortOrder);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "分类排序更新成功");
        return ResponseEntity.ok(response);
    }
}