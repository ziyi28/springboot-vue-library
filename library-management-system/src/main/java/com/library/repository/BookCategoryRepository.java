package com.library.repository;

import com.library.model.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

    // 根据分类名称查找
    Optional<BookCategory> findByCategoryName(String categoryName);

    // 根据代码查找
    Optional<BookCategory> findByCode(String code);

    // 查找根分类（父分类ID为空）
    List<BookCategory> findByParentIdIsNull();

    // 根据父分类ID查找子分类
    List<BookCategory> findByParentId(Long parentId);

    // 根据状态查找分类
    List<BookCategory> findByStatus(Boolean status);

    // 查找所有启用的分类
    List<BookCategory> findByStatusTrue();

    // 根据排序顺序查找分类
    List<BookCategory> findByOrderBySortOrderAsc();

    // 根据排序顺序和状态查找分类
    List<BookCategory> findByStatusOrderBySortOrderAsc(Boolean status);

    // 检查分类名称是否存在
    boolean existsByCategoryName(String categoryName);

    // 检查分类代码是否存在
    boolean existsByCode(String code);

    // 模糊搜索分类（分类名称、代码、描述）
    @Query("SELECT c FROM BookCategory c WHERE " +
           "LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BookCategory> searchCategories(@Param("keyword") String keyword);

    // 统计每个分类下的图书数量
    @Query("SELECT c.id, c.categoryName, COUNT(b) FROM BookCategory c " +
           "LEFT JOIN c.books b " +
           "GROUP BY c.id, c.categoryName")
    List<Object[]> countBooksByCategory();

    // 查找没有图书的分类
    @Query("SELECT c FROM BookCategory c WHERE SIZE(c.books) = 0")
    List<BookCategory> findEmptyCategories();

    // 查找有图书的分类
    @Query("SELECT c FROM BookCategory c WHERE SIZE(c.books) > 0")
    List<BookCategory> findCategoriesWithBooks();

    // 根据父分类ID和状态查找子分类
    List<BookCategory> findByParentIdAndStatus(Long parentId, Boolean status);

    // 查找所有一级分类（状态为启用）
    List<BookCategory> findByParentIdIsNullAndStatusTrueOrderBySortOrderAsc();

    // 根据分类名称模糊查找（启用状态）
    @Query("SELECT c FROM BookCategory c WHERE c.status = true AND " +
           "LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<BookCategory> findActiveCategoriesByNameContaining(@Param("name") String name);

    // 获取分类树结构（需要递归处理）
    @Query("SELECT c FROM BookCategory c WHERE c.parentId IS NULL ORDER BY c.sortOrder ASC")
    List<BookCategory> findRootCategories();

    // 统计活跃和禁用的分类数量
    @Query("SELECT c.status, COUNT(c) FROM BookCategory c GROUP BY c.status")
    List<Object[]> countByStatus();

    // 查找最热门的分类（有最多图书的分类）
    @Query("SELECT c.id, c.categoryName, COUNT(b) as bookCount FROM BookCategory c " +
           "JOIN c.books b " +
           "GROUP BY c.id, c.categoryName " +
           "ORDER BY bookCount DESC")
    List<Object[]> findPopularCategories();
}