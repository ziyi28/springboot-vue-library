package com.library.service.impl;

import com.library.model.BookCategory;
import com.library.repository.BookCategoryRepository;
import com.library.repository.BookRepository;
import com.library.service.BookCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 图书分类服务实现类
 */
@Service
public class BookCategoryServiceImpl implements BookCategoryService {

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public BookCategory save(BookCategory category) {
        if (category.getId() == null) {
            // 新建分类
            category.setCreateTime(LocalDateTime.now());
        }
        category.setUpdateTime(LocalDateTime.now());

        // 设置默认值
        if (category.getStatus() == null) {
            category.setStatus(true);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }

        return bookCategoryRepository.save(category);
    }

    @Override
    public Optional<BookCategory> findById(Long id) {
        return bookCategoryRepository.findById(id);
    }

    @Override
    public Page<BookCategory> findAll(Pageable pageable) {
        return bookCategoryRepository.findAll(pageable);
    }

    @Override
    public List<BookCategory> findAllActive() {
        return bookCategoryRepository.findByStatusTrueOrderBySortOrderAsc();
    }

    @Override
    public void deleteById(Long id) {
        bookCategoryRepository.deleteById(id);
    }

    @Override
    public boolean existsByCategoryName(String categoryName) {
        return bookCategoryRepository.existsByCategoryName(categoryName);
    }

    @Override
    public boolean existsByCode(String code) {
        return bookCategoryRepository.existsByCode(code);
    }

    @Override
    public List<BookCategory> findByCategoryNameContaining(String keyword) {
        return bookCategoryRepository.findByCategoryNameContainingIgnoreCaseOrderByCategoryNameAsc(keyword);
    }

    @Override
    public Page<BookCategory> searchCategories(String keyword, Pageable pageable) {
        return bookCategoryRepository.findByCategoryNameContainingIgnoreCase(keyword, pageable);
    }

    @Override
    public boolean hasBooks(Long categoryId) {
        return bookRepository.countByCategoryId(categoryId) > 0;
    }

    @Override
    public Object getCategoryStats() {
        Map<String, Object> stats = new HashMap<>();

        // 总分类数
        stats.put("totalCategories", bookCategoryRepository.count());

        // 活跃分类数
        stats.put("activeCategories", bookCategoryRepository.countByStatusTrue());

        // 各分类的图书数量
        List<Object[]> categoryBookCounts = bookCategoryRepository.countBooksByCategory();
        stats.put("categoryBookCounts", categoryBookCounts);

        return stats;
    }

    @Override
    public List<BookCategory> findByParentId(Long parentId) {
        if (parentId == null) {
            return bookCategoryRepository.findByParentIdIsNullOrderBySortOrderAsc();
        } else {
            return bookCategoryRepository.findByParentId(parentId);
        }
    }

    @Override
    public List<BookCategory> findRootCategories() {
        return bookCategoryRepository.findByParentIdIsNullOrderBySortOrderAsc();
    }

    @Override
    public long countCategories() {
        return bookCategoryRepository.count();
    }

    @Override
    public long countActiveCategories() {
        return bookCategoryRepository.countByStatusTrue();
    }
}