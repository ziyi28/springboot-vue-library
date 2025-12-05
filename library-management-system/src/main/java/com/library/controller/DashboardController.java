package com.library.controller;

import com.library.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表板控制器
 * 提供系统统计数据和仪表板信息
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    /**
     * 获取系统总体统计信息
     */
    @GetMapping("/overview")
    public ResponseEntity<?> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();

        // 用户统计
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findByStatus(1).size();
        overview.put("totalUsers", totalUsers);
        overview.put("activeUsers", activeUsers);
        overview.put("inactiveUsers", totalUsers - activeUsers);

        // 管理员统计
        long totalAdmins = adminRepository.count();
        long activeAdmins = adminRepository.findByStatusTrue().size();
        overview.put("totalAdmins", totalAdmins);
        overview.put("activeAdmins", activeAdmins);

        // 图书统计
        long totalBooks = bookRepository.count();
        long availableBooks = bookRepository.findByStatus(1).stream()
                .filter(book -> book.getAvailableCopies() > 0).count();
        long borrowedBooks = borrowRecordRepository.findByStatus(1).size();
        overview.put("totalBooks", totalBooks);
        overview.put("availableBooks", availableBooks);
        overview.put("borrowedBooks", borrowedBooks);

        // 分类统计
        long totalCategories = bookCategoryRepository.count();
        long activeCategories = bookCategoryRepository.findByStatusTrue().size();
        overview.put("totalCategories", totalCategories);
        overview.put("activeCategories", activeCategories);

        // 借阅统计
        long totalBorrows = borrowRecordRepository.count();
        long activeBorrows = borrowRecordRepository.findByStatus(1).size();
        long overdueBorrows = borrowRecordRepository.findByStatus(3).size();
        overview.put("totalBorrows", totalBorrows);
        overview.put("activeBorrows", activeBorrows);
        overview.put("overdueBorrows", overdueBorrows);

        // 收藏统计
        long totalFavorites = favoriteRepository.count();
        long favoritedBooks = favoriteRepository.countDistinctBookId();
        overview.put("totalFavorites", totalFavorites);
        overview.put("favoritedBooks", favoritedBooks);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", overview);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取月度统计信息
     */
    @GetMapping("/monthly-stats")
    public ResponseEntity<?> getMonthlyStats(@RequestParam(defaultValue = "12") int months) {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);
        LocalDateTime endDate = LocalDateTime.now();

        // 月度借阅统计
        List<Object[]> monthlyBorrows = borrowRecordRepository.findMonthlyBorrowStats(startDate, endDate);

        // 月度注册用户统计
        List<Object[]> monthlyUsers = userRepository.findMonthlyUserStats(startDate, endDate);

        // 月度收藏统计
        List<Object[]> monthlyFavorites = favoriteRepository.findFavoriteCountByMonth();

        Map<String, Object> stats = new HashMap<>();
        stats.put("monthlyBorrows", monthlyBorrows);
        stats.put("monthlyUsers", monthlyUsers);
        stats.put("monthlyFavorites", monthlyFavorites);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取热门图书
     */
    @GetMapping("/popular-books")
    public ResponseEntity<?> getPopularBooks(@RequestParam(defaultValue = "10") int limit) {
        List<Object[]> popularBooks = borrowRecordRepository.findPopularBooks(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", popularBooks);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取活跃用户（借阅次数最多的用户）
     */
    @GetMapping("/active-users")
    public ResponseEntity<?> getActiveUsers(@RequestParam(defaultValue = "10") int limit) {
        List<Object[]> activeUsers = borrowRecordRepository.findActiveUsers(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", activeUsers);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取图书分类统计
     */
    @GetMapping("/category-stats")
    public ResponseEntity<?> getCategoryStats() {
        // 各分类图书数量
        List<Object[]> bookCountByCategory = bookCategoryRepository.findBookCountByCategory();

        // 各分类借阅次数
        List<Object[]> borrowCountByCategory = borrowRecordRepository.findBorrowCountByCategory();

        Map<String, Object> stats = new HashMap<>();
        stats.put("bookCountByCategory", bookCountByCategory);
        stats.put("borrowCountByCategory", borrowCountByCategory);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取今日统计
     */
    @GetMapping("/today-stats")
    public ResponseEntity<?> getTodayStats() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // 今日注册用户数
        long todayUsers = userRepository.countByCreateTimeBetween(startOfDay, endOfDay);

        // 今日借阅数
        long todayBorrows = borrowRecordRepository.countByBorrowDateBetween(startOfDay, endOfDay);

        // 今日归还数
        long todayReturns = borrowRecordRepository.countByReturnDateBetween(startOfDay, endOfDay);

        // 今日收藏数
        long todayFavorites = favoriteRepository.countByCreateTimeBetween(startOfDay, endOfDay);

        Map<String, Object> todayStats = new HashMap<>();
        todayStats.put("todayUsers", todayUsers);
        todayStats.put("todayBorrows", todayBorrows);
        todayStats.put("todayReturns", todayReturns);
        todayStats.put("todayFavorites", todayFavorites);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", todayStats);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取即将到期的借阅记录
     */
    @GetMapping("/due-soon")
    public ResponseEntity<?> getDueSoonBooks(@RequestParam(defaultValue = "7") int days) {
        LocalDateTime fromDate = LocalDateTime.now();
        LocalDateTime toDate = fromDate.plusDays(days);

        List<Object[]> dueSoonBooks = borrowRecordRepository.findDueSoonBooks(fromDate, toDate);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", dueSoonBooks);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取逾期记录
     */
    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueBooks() {
        List<Object[]> overdueBooks = borrowRecordRepository.findOverdueBooks();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", overdueBooks);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取系统健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<?> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();

        // 数据库连接状态
        try {
            userRepository.count();
            health.put("database", "healthy");
        } catch (Exception e) {
            health.put("database", "unhealthy");
        }

        // 系统负载情况
        long activeConnections = borrowRecordRepository.findByStatus(1).size();
        health.put("activeConnections", activeConnections);

        // 存储空间使用情况（模拟）
        health.put("diskUsage", "45%");

        // 内存使用情况（模拟）
        health.put("memoryUsage", "62%");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", health);

        return ResponseEntity.ok(response);
    }
}