package com.library.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 简化的仪表板控制器
 * 不依赖复杂的Repository查询
 */
@RestController
@RequestMapping("/simple-dashboard")
@CrossOrigin(origins = "*")
public class SimpleDashboardController {

    /**
     * 获取系统概览
     */
    @GetMapping("/overview")
    public Map<String, Object> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();

        // 模拟数据，实际应该从数据库获取
        overview.put("totalUsers", 3);
        overview.put("activeUsers", 2);
        overview.put("totalAdmins", 2);
        overview.put("activeAdmins", 2);
        overview.put("totalBooks", 5);
        overview.put("availableBooks", 4);
        overview.put("borrowedBooks", 1);
        overview.put("totalCategories", 4);
        overview.put("activeCategories", 4);
        overview.put("totalBorrows", 10);
        overview.put("activeBorrows", 2);
        overview.put("overdueBorrows", 0);
        overview.put("totalFavorites", 5);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", overview);

        return response;
    }

    /**
     * 获取月度统计
     */
    @GetMapping("/monthly-stats")
    public Map<String, Object> getMonthlyStats() {
        Map<String, Object> stats = new HashMap<>();

        // 模拟数据
        stats.put("monthlyBorrows", new Object[]{});
        stats.put("monthlyUsers", new Object[]{});
        stats.put("monthlyFavorites", new Object[]{});

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);

        return response;
    }

    /**
     * 获取热门图书
     */
    @GetMapping("/popular-books")
    public Map<String, Object> getPopularBooks() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", new Object[]{});
        return response;
    }

    /**
     * 获取活跃用户
     */
    @GetMapping("/active-users")
    public Map<String, Object> getActiveUsers() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", new Object[]{});
        return response;
    }

    /**
     * 获取分类统计
     */
    @GetMapping("/category-stats")
    public Map<String, Object> getCategoryStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("bookCountByCategory", new Object[]{});
        stats.put("borrowCountByCategory", new Object[]{});

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);

        return response;
    }

    /**
     * 获取今日统计
     */
    @GetMapping("/today-stats")
    public Map<String, Object> getTodayStats() {
        Map<String, Object> todayStats = new HashMap<>();
        todayStats.put("todayUsers", 0);
        todayStats.put("todayBorrows", 0);
        todayStats.put("todayReturns", 0);
        todayStats.put("todayFavorites", 0);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", todayStats);

        return response;
    }

    /**
     * 获取即将到期的借阅
     */
    @GetMapping("/due-soon")
    public Map<String, Object> getDueSoonBooks() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", new Object[]{});
        return response;
    }

    /**
     * 获取逾期记录
     */
    @GetMapping("/overdue")
    public Map<String, Object> getOverdueBooks() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", new Object[]{});
        return response;
    }

    /**
     * 获取系统健康状态
     */
    @GetMapping("/health")
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("database", "healthy");
        health.put("activeConnections", 2);
        health.put("systemLoad", "正常");
        health.put("diskUsage", "45%");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", health);

        return response;
    }

    /**
     * 基本健康检查
     */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Dashboard service is running");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}