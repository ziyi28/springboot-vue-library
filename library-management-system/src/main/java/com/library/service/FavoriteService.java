package com.library.service;

import com.library.model.Favorite;
import com.library.model.Book;
import com.library.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FavoriteService {

    // 基础CRUD操作
    Favorite addFavorite(String username, Long bookId);
    void removeFavorite(String username, Long bookId);
    List<Favorite> getUserFavorites(String username);
    Page<Favorite> getUserFavorites(String username, Pageable pageable);
    Page<Favorite> getFavoritesByUserId(Long userId, Pageable pageable);
    List<Favorite> getBookFavorites(Long bookId);
    Optional<Favorite> getFavorite(Long userId, Long bookId);

    // 查询操作
    boolean isFavorited(String username, Long bookId);
    boolean isBookFavorited(Long userId, Long bookId);
    long getUserFavoriteCount(String username);
    long getUserFavoriteCount(Long userId);
    long getBookFavoriteCount(Long bookId);
    List<Favorite> getRecentUserFavorites(Long userId);
    List<Favorite> getRecentFavorites();

    // 统计分析
    long getTotalFavoriteCount();
    long getActiveUserCount();
    long getFavoritedBookCount();
    List<Object[]> getPopularBooks();
    List<Object[]> getPopularCategories();
    List<Object[]> getFavoriteCountByDate();
    List<Object[]> getFavoriteCountByMonth();
    List<Object[]> getTopUsersByFavoriteCount();

    // 热门图书分析
    List<Map<String, Object>> getPopularBooks(int limit);

    // 搜索和筛选
    List<Favorite> searchUserFavorites(Long userId, String keyword);
    List<Favorite> getFavoritesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Favorite> getUserFavoritesByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    // 用户分类分析
    List<Object[]> getUserFavoriteCategories(Long userId);
    boolean hasUserFavoritedCategory(Long userId, Long categoryId);

    // 批量操作
    void addMultipleFavorites(Long userId, List<Long> bookIds);
    void removeMultipleFavorites(Long userId, List<Long> bookIds);
    int batchDeleteFavorites(List<Long> favoriteIds);
    void clearUserFavorites(Long userId);
    void clearBookFavorites(Long bookId);
}