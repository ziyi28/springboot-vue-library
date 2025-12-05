package com.library.service;

import com.library.model.BookCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 图书分类服务接口
 */
public interface BookCategoryService {

    // 基本CRUD操作
    BookCategory save(BookCategory category);

    Optional<BookCategory> findById(Long id);

    Page<BookCategory> findAll(Pageable pageable);

    List<BookCategory> findAllActive();

    void deleteById(Long id);

    // 查询方法
    boolean existsByCategoryName(String categoryName);

    boolean existsByCode(String code);

    List<BookCategory> findByCategoryNameContaining(String keyword);

    Page<BookCategory> searchCategories(String keyword, Pageable pageable);

    // 业务方法
    boolean hasBooks(Long categoryId);

    Object getCategoryStats();

    List<BookCategory> findByParentId(Long parentId);

    List<BookCategory> findRootCategories();

    long countCategories();

    long countActiveCategories();
}