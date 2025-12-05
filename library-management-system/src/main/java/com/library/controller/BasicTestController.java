package com.library.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 基础测试控制器
 * 不依赖数据库，仅测试基本的HTTP功能
 */
@RestController
@RequestMapping("/basic")
@CrossOrigin(origins = "*")
public class BasicTestController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "pong");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("received", data);
        response.put("echo", "服务器已收到您的数据");
        return response;
    }

    @GetMapping("/login-test")
    public Map<String, Object> loginTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登录接口可用");
        response.put("testAccounts", new String[]{
            "admin/admin123 (管理员)",
            "librarian/admin123 (图书管理员)",
            "student1/123456 (学生)"
        });
        return response;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "图书管理系统");
        response.put("status", "运行中");
        response.put("description", "这是一个测试接口，用于验证基本功能");
        return response;
    }
}