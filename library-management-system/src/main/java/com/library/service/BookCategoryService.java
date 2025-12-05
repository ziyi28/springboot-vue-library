package com.library.service;

import com.library.model.BookCategory;

import java.util.List;
import java.util.Optional;

public interface BookCategoryService {

    // 基础CRUD操作
    BookCategory createCategory(BookCategory category);
    Optional<BookCategory> getCategoryById(Long id);
    List<BookCategory> getAllCategories();
    BookCategory updateCategory(Long id, BookCategory category);
    void deleteCategory(Long id);

    // 查询操作
    Optional<BookCategory> getCategoryByName(String categoryName);
    Optional<BookCategory> getCategoryByCode(String code);
    List<BookCategory> getRootCategories();
    List<BookCategory> getSubCategories(Long parentId);
    List<BookCategory> getActiveCategories();
    List<BookCategory> getCategoriesByStatus(Boolean status);

    // 层级结构操作
    List<BookCategory> getCategoryTree();
    List<BookCategory> getActiveRootCategories();
    List<BookCategory> getActiveSubCategories(Long parentId);
    boolean hasSubCategories(Long categoryId);

    // 搜索和筛选
    List<BookCategory> searchCategories(String keyword);
    List<BookCategory> getCategoriesByNameContaining(String name);
    List<BookCategory> getCategoriesByOrder();

    // 状态管理
    void enableCategory(Long categoryId);
    void disableCategory(Long categoryId);
    List<BookCategory> getDisabledCategories();

    // 业务统计
    long getTotalCategoryCount();
    long getActiveCategoryCount();
    long getDisabledCategoryCount();
    List<Object[]> getBookCountByCategory();
    List<Object[]> getCategoryStatusCount();
    List<Object[]> getPopularCategories();
    List<BookCategory> getEmptyCategories();
    List<BookCategory> getCategoriesWithBooks();

    // 验证操作
    boolean existsByCategoryName(String categoryName);
    boolean existsByCode(String code);
    boolean isCategoryNameAvailable(String categoryName);
    boolean isCodeAvailable(String code);
    boolean canDeleteCategory(Long categoryId);

    // 业务操作
    BookCategory createRootCategory(String categoryName, String code, String description);
    BookCategory createSubCategory(String categoryName, String code, String description, Long parentId);
    void moveSubCategory(Long categoryId, Long newParentId);
    void updateCategoryOrder(Long categoryId, Integer sortOrder);

    // 图书关联操作
    long getBookCountByCategory(Long categoryId);
    boolean hasBooksInCategory(Long categoryId);
    List<BookCategory> getCategoriesWithBookCount();

    // 分页查询
    List<BookCategory> getCategoriesByPage(int page, int size);
    List<BookCategory> getActiveCategoriesByPage(int page, int size);

    // 批量操作
    void enableCategories(List<Long> categoryIds);
    void disableCategories(List<Long> categoryIds);
    void deleteCategories(List<Long> categoryIds);

    // 导入导出操作
    String exportCategoriesToCsv();
    List<BookCategory> importCategoriesFromCsv(String csvContent);

    // 树形结构处理
    List<BookCategory> buildCategoryHierarchy();
    List<BookCategory> getChildrenCategories(Long categoryId);
    List<BookCategory> getParentCategories(Long categoryId);
    int getCategoryDepth(Long categoryId);
    String getCategoryPath(Long categoryId);
}