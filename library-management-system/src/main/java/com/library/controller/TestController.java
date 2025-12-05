package com.library.controller;

import com.library.repository.AdminRepository;
import com.library.repository.UserRepository;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于验证系统是否正常工作
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * 系统状态检查
     */
    @GetMapping("/status")
    public ResponseEntity<?> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            long adminCount = adminRepository.count();
            long userCount = userRepository.count();
            long bookCount = bookRepository.count();

            status.put("success", true);
            status.put("database", "connected");
            status.put("adminCount", adminCount);
            status.put("userCount", userCount);
            status.put("bookCount", bookCount);
            status.put("message", "系统运行正常");

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            status.put("success", false);
            status.put("database", "error");
            status.put("message", "数据库连接失败: " + e.getMessage());

            return ResponseEntity.status(500).body(status);
        }
    }

    /**
     * 获取测试账户信息
     */
    @GetMapping("/accounts")
    public ResponseEntity<?> getTestAccounts() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 获取管理员账户
            Map<String, Object> admin = adminRepository.findByUsername("admin")
                .map(a -> {
                    Map<String, Object> adminInfo = new HashMap<>();
                    adminInfo.put("username", a.getUsername());
                    adminInfo.put("realName", a.getRealName());
                    adminInfo.put("role", a.getRole());
                    adminInfo.put("status", a.getStatus());
                    return adminInfo;
                })
                .orElse(null);

            // 获取图书管理员账户
            Map<String, Object> librarian = adminRepository.findByUsername("librarian")
                .map(a -> {
                    Map<String, Object> libInfo = new HashMap<>();
                    libInfo.put("username", a.getUsername());
                    libInfo.put("realName", a.getRealName());
                    libInfo.put("role", a.getRole());
                    libInfo.put("status", a.getStatus());
                    return libInfo;
                })
                .orElse(null);

            // 获取测试用户
            Map<String, Object> student = userRepository.findByUsername("student1")
                .map(u -> {
                    Map<String, Object> studentInfo = new HashMap<>();
                    studentInfo.put("username", u.getUsername());
                    studentInfo.put("realName", u.getRealName());
                    studentInfo.put("role", u.getRole());
                    studentInfo.put("status", u.getStatus());
                    return studentInfo;
                })
                .orElse(null);

            response.put("success", true);
            response.put("admin", admin);
            response.put("librarian", librarian);
            response.put("student", student);
            response.put("note", "所有账户的默认密码都是: admin123 或 123456");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取账户信息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 简单的ping测试
     */
    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "pong");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}