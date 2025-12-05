package com.library.repository;

import com.library.model.Favorite;
import com.library.model.User;
import com.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 根据用户查找所有收藏
    List<Favorite> findByUserId(Long userId);

    // 根据图书查找所有收藏用户
    List<Favorite> findByBookId(Long bookId);

    // 根据用户查找所有收藏（使用User对象）
    List<Favorite> findByUser(User user);

    // 根据图书查找所有收藏用户（使用Book对象）
    List<Favorite> findByBook(Book book);

    // 检查用户是否已收藏某本图书
    Optional<Favorite> findByUserIdAndBookId(Long userId, Long bookId);

    // 检查用户是否已收藏某本图书（使用对象）
    Optional<Favorite> findByUserAndBook(User user, Book book);

    // 删除用户的某个收藏
    void deleteByUserIdAndBookId(Long userId, Long bookId);

    // 统计用户的收藏数量
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    // 统计图书被收藏的数量
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.book.id = :bookId")
    Long countByBookId(@Param("bookId") Long bookId);

    // 查找用户最近收藏的图书
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId ORDER BY f.createTime DESC")
    List<Favorite> findRecentByUserId(@Param("userId") Long userId);

    // 查找指定时间范围内的收藏
    @Query("SELECT f FROM Favorite f WHERE f.createTime BETWEEN :startDate AND :endDate")
    List<Favorite> findByCreateTimeBetween(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    // 查找指定时间范围内用户的收藏
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.createTime BETWEEN :startDate AND :endDate")
    List<Favorite> findByUserIdAndCreateTimeBetween(@Param("userId") Long userId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    // 查找热门收藏的图书（被收藏次数最多的）
    @Query("SELECT f.book.id, f.book.title, COUNT(f) as favoriteCount FROM Favorite f " +
           "GROUP BY f.book.id, f.book.title " +
           "ORDER BY favoriteCount DESC")
    List<Object[]> findPopularBooks();

    // 查找最受欢迎的分类（基于收藏数据）
    @Query("SELECT b.category.id, b.category.categoryName, COUNT(f) as favoriteCount " +
           "FROM Favorite f " +
           "JOIN f.book b " +
           "GROUP BY b.category.id, b.category.categoryName " +
           "ORDER BY favoriteCount DESC")
    List<Object[]> findPopularCategories();

    // 统计每日收藏数量
    @Query("SELECT FUNCTION('DATE', f.createTime), COUNT(f) FROM Favorite f " +
           "GROUP BY FUNCTION('DATE', f.createTime) " +
           "ORDER BY FUNCTION('DATE', f.createTime) DESC")
    List<Object[]> countFavoritesByDate();

    // 统计每月收藏数量
    @Query("SELECT FUNCTION('YEAR', f.createTime), FUNCTION('MONTH', f.createTime), COUNT(f) FROM Favorite f " +
           "GROUP BY FUNCTION('YEAR', f.createTime), FUNCTION('MONTH', f.createTime) " +
           "ORDER BY FUNCTION('YEAR', f.createTime) DESC, FUNCTION('MONTH', f.createTime) DESC")
    List<Object[]> countFavoritesByMonth();

    // 查找收藏数量最多的用户
    @Query("SELECT f.user.id, f.user.username, COUNT(f) as favoriteCount FROM Favorite f " +
           "GROUP BY f.user.id, f.user.username " +
           "ORDER BY favoriteCount DESC")
    List<Object[]> findTopUsersByFavoriteCount();

    // 模糊搜索用户的收藏（根据图书标题）
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND " +
           "LOWER(f.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Favorite> searchUserFavorites(@Param("userId") Long userId, @Param("keyword") String keyword);

    // 查找收藏了某分类下图书的用户
    @Query("SELECT DISTINCT f.user FROM Favorite f WHERE f.book.category.id = :categoryId")
    List<User> findUsersWhoFavoritedCategory(@Param("categoryId") Long categoryId);

    // 查找用户的收藏总数统计
    @Query("SELECT COUNT(f) FROM Favorite f")
    Long totalFavoritesCount();

    // 查找最近添加的收藏（全局）
    @Query("SELECT f FROM Favorite f ORDER BY f.createTime DESC")
    List<Favorite> findRecentFavorites();

    // 查找用户收藏的图书分类分布
    @Query("SELECT b.category.categoryName, COUNT(f) FROM Favorite f " +
           "JOIN f.book b " +
           "WHERE f.user.id = :userId " +
           "GROUP BY b.category.categoryName " +
           "ORDER BY COUNT(f) DESC")
    List<Object[]> getUserFavoriteCategories(@Param("userId") Long userId);

    // 检查用户是否收藏了指定分类的任何图书
    @Query("SELECT COUNT(f) > 0 FROM Favorite f WHERE f.user.id = :userId AND f.book.category.id = :categoryId")
    boolean hasUserFavoritedCategory(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    // 查找没有收藏任何图书的用户
    @Query("SELECT u FROM User u WHERE NOT EXISTS (SELECT 1 FROM Favorite f WHERE f.user.id = u.id)")
    List<User> findUsersWithoutFavorites();

    // 新增缺失的方法
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId ORDER BY f.createTime DESC")
    List<Favorite> findByUserIdOrderByCreateTimeDesc(@Param("userId") Long userId);

    @Query("SELECT f FROM Favorite f ORDER BY f.createTime DESC")
    List<Favorite> findAllByOrderByCreateTimeDesc();

    @Query("SELECT f.book.id, f.book.title, f.book.author, COUNT(f) as favoriteCount FROM Favorite f " +
           "GROUP BY f.book.id, f.book.title, f.book.author " +
           "ORDER BY favoriteCount DESC")
    List<Object[]> findFavoriteCountByBook();

    @Query("SELECT b.category.id, b.category.categoryName, COUNT(f) as favoriteCount " +
           "FROM Favorite f " +
           "JOIN f.book b " +
           "GROUP BY b.category.id, b.category.categoryName " +
           "ORDER BY favoriteCount DESC")
    List<Object[]> findFavoriteCountByCategory();

    @Query("SELECT FUNCTION('DATE', f.createTime), COUNT(f) FROM Favorite f " +
           "GROUP BY FUNCTION('DATE', f.createTime) " +
           "ORDER BY FUNCTION('DATE', f.createTime) DESC")
    List<Object[]> findFavoriteCountByDate();

    @Query("SELECT FUNCTION('YEAR', f.createTime), FUNCTION('MONTH', f.createTime), COUNT(f) FROM Favorite f " +
           "GROUP BY FUNCTION('YEAR', f.createTime), FUNCTION('MONTH', f.createTime) " +
           "ORDER BY FUNCTION('YEAR', f.createTime) DESC, FUNCTION('MONTH', f.createTime) DESC")
    List<Object[]> findFavoriteCountByMonth();

    @Query("SELECT f.user.id, f.user.username, COUNT(f) as favoriteCount FROM Favorite f " +
           "GROUP BY f.user.id, f.user.username " +
           "ORDER BY favoriteCount DESC")
    List<Object[]> findTopUsersByFavoriteCount();

    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND " +
           "LOWER(f.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Favorite> findByUserIdAndBookTitleContaining(@Param("userId") Long userId, @Param("keyword") String keyword);

    @Query("SELECT b.category.categoryName, COUNT(f) FROM Favorite f " +
           "JOIN f.book b " +
           "WHERE f.user.id = :userId " +
           "GROUP BY b.category.categoryName " +
           "ORDER BY COUNT(f) DESC")
    List<Object[]> findFavoriteCountByCategoryForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) > 0 FROM Favorite f WHERE f.user.id = :userId AND f.book.category.id = :categoryId")
    boolean existsByUserIdAndBookCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(DISTINCT f.book.id) FROM Favorite f")
    Long countDistinctBookId();

    void deleteByUserId(Long userId);

    void deleteByBookId(Long bookId);

    // 为了支持Pageable查询
    org.springframework.data.domain.Page<Favorite> findByUserId(Long userId, org.springframework.data.domain.Pageable pageable);
}