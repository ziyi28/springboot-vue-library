package com.library.controller;

import com.library.model.Favorite;
import com.library.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 收藏控制器
 * 管理用户图书收藏功能
 */
@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addFavorite(@RequestBody Map<String, Long> request) {
        Long bookId = request.get("bookId");
        if (bookId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "图书ID不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 获取当前用户名
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Favorite favorite = favoriteService.addFavorite(username, bookId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "收藏添加成功");
            response.put("data", favorite);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/book/{bookId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeFavorite(@PathVariable Long bookId) {
        try {
            // 获取当前用户名
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            favoriteService.removeFavorite(username, bookId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "收藏已取消");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取当前用户的收藏列表
     */
    @GetMapping("/my-favorites")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMyFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 获取当前用户名
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Pageable pageable = PageRequest.of(page, size);
        Page<Favorite> favorites = favoriteService.getUserFavorites(username, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", favorites.getContent());
        response.put("currentPage", favorites.getNumber());
        response.put("totalItems", favorites.getTotalElements());
        response.put("totalPages", favorites.getTotalPages());
        response.put("pageSize", favorites.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check/{bookId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkFavorite(@PathVariable Long bookId) {
        // 获取当前用户名
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        boolean isFavorited = favoriteService.isFavorited(username, bookId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("favorited", isFavorited);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取收藏总数
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getFavoriteCount() {
        // 获取当前用户名
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        long count = favoriteService.getUserFavoriteCount(username);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", count);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取指定用户的收藏列表（管理员功能）
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> getUserFavorites(@PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Favorite> favorites = favoriteService.getFavoritesByUserId(userId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", favorites.getContent());
        response.put("currentPage", favorites.getNumber());
        response.put("totalItems", favorites.getTotalElements());
        response.put("totalPages", favorites.getTotalPages());
        response.put("pageSize", favorites.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 获取指定图书的收藏用户列表（管理员功能）
     */
    @GetMapping("/book/{bookId}/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> getBookFavoriteUsers(@PathVariable Long bookId) {
        List<Favorite> favorites = favoriteService.getBookFavorites(bookId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", favorites);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取收藏统计信息
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> getFavoriteStats() {
        long totalFavorites = favoriteService.getTotalFavoriteCount();
        long activeUsers = favoriteService.getActiveUserCount();
        long favoritedBooks = favoriteService.getFavoritedBookCount();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFavorites", totalFavorites);
        stats.put("activeUsers", activeUsers);
        stats.put("favoritedBooks", favoritedBooks);
        stats.put("averageFavoritesPerUser", activeUsers > 0 ? (double) totalFavorites / activeUsers : 0);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取热门收藏图书
     */
    @GetMapping("/popular-books")
    public ResponseEntity<?> getPopularBooks(@RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> popularBooks = favoriteService.getPopularBooks(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", popularBooks);

        return ResponseEntity.ok(response);
    }

    /**
     * 批量删除收藏（管理员功能）
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> batchDeleteFavorites(@RequestBody List<Long> favoriteIds) {
        try {
            int deletedCount = favoriteService.batchDeleteFavorites(favoriteIds);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "成功删除 " + deletedCount + " 条收藏记录");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}