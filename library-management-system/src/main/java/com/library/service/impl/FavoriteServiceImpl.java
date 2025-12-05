package com.library.service.impl;

import com.library.model.Favorite;
import com.library.model.Book;
import com.library.model.User;
import com.library.repository.FavoriteRepository;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import com.library.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Favorite addFavorite(String username, Long bookId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));

        // 检查是否已经收藏
        if (favoriteRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new RuntimeException("该图书已在收藏列表中");
        }

        Favorite favorite = new Favorite(user, book);
        return favoriteRepository.save(favorite);
    }

    @Override
    public void removeFavorite(String username, Long bookId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Optional<Favorite> favorite = favoriteRepository.findByUserIdAndBookId(user.getId(), bookId);
        if (favorite.isPresent()) {
            favoriteRepository.delete(favorite.get());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getUserFavorites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return favoriteRepository.findByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Favorite> getUserFavorites(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return favoriteRepository.findByUserId(user.getId(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Favorite> getFavoritesByUserId(Long userId, Pageable pageable) {
        return favoriteRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getBookFavorites(Long bookId) {
        return favoriteRepository.findByBookId(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Favorite> getFavorite(Long userId, Long bookId) {
        return favoriteRepository.findByUserIdAndBookId(userId, bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorited(String username, Long bookId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return favoriteRepository.existsByUserIdAndBookId(user.getId(), bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookFavorited(Long userId, Long bookId) {
        return favoriteRepository.existsByUserIdAndBookId(userId, bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserFavoriteCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return favoriteRepository.countByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserFavoriteCount(Long userId) {
        return favoriteRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getBookFavoriteCount(Long bookId) {
        return favoriteRepository.countByBookId(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getRecentUserFavorites(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getRecentFavorites() {
        return favoriteRepository.findAllByOrderByCreateTimeDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalFavoriteCount() {
        return favoriteRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getFavoritedBookCount() {
        return favoriteRepository.countDistinctBookId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPopularBooks() {
        return favoriteRepository.findFavoriteCountByBook();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPopularCategories() {
        return favoriteRepository.findFavoriteCountByCategory();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getFavoriteCountByDate() {
        return favoriteRepository.findFavoriteCountByDate();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getFavoriteCountByMonth() {
        return favoriteRepository.findFavoriteCountByMonth();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTopUsersByFavoriteCount() {
        return favoriteRepository.findTopUsersByFavoriteCount();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPopularBooks(int limit) {
        List<Object[]> results = favoriteRepository.findFavoriteCountByBook();
        return results.stream()
                .limit(limit)
                .map(result -> {
                    Map<String, Object> bookInfo = new HashMap<>();
                    bookInfo.put("bookId", result[0]);
                    bookInfo.put("bookTitle", result[1]);
                    bookInfo.put("author", result[2]);
                    bookInfo.put("favoriteCount", result[3]);
                    return bookInfo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> searchUserFavorites(Long userId, String keyword) {
        return favoriteRepository.findByUserIdAndBookTitleContaining(userId, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getFavoritesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return favoriteRepository.findByCreateTimeBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorite> getUserFavoritesByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return favoriteRepository.findByUserIdAndCreateTimeBetween(userId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getUserFavoriteCategories(Long userId) {
        return favoriteRepository.findFavoriteCountByCategoryForUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserFavoritedCategory(Long userId, Long categoryId) {
        return favoriteRepository.existsByUserIdAndBookCategoryId(userId, categoryId);
    }

    @Override
    public void addMultipleFavorites(Long userId, List<Long> bookIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        for (Long bookId : bookIds) {
            if (!favoriteRepository.existsByUserIdAndBookId(userId, bookId)) {
                Book book = bookRepository.findById(bookId)
                        .orElseThrow(() -> new RuntimeException("图书不存在，ID: " + bookId));

                Favorite favorite = new Favorite(user, book);
                favoriteRepository.save(favorite);
            }
        }
    }

    @Override
    public void removeMultipleFavorites(Long userId, List<Long> bookIds) {
        for (Long bookId : bookIds) {
            Optional<Favorite> favorite = favoriteRepository.findByUserIdAndBookId(userId, bookId);
            favorite.ifPresent(favoriteRepository::delete);
        }
    }

    @Override
    public int batchDeleteFavorites(List<Long> favoriteIds) {
        List<Favorite> favorites = favoriteRepository.findAllById(favoriteIds);
        favoriteRepository.deleteAll(favorites);
        return favorites.size();
    }

    @Override
    public void clearUserFavorites(Long userId) {
        favoriteRepository.deleteByUserId(userId);
    }

    @Override
    public void clearBookFavorites(Long bookId) {
        favoriteRepository.deleteByBookId(bookId);
    }
}