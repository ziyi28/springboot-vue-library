package com.library.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单登录控制器
 * 不依赖数据库，仅用于测试登录功能
 */
@RestController
@RequestMapping("/simple-login")
@CrossOrigin(origins = "*")
public class SimpleLoginController {

    @PostMapping("/test")
    public Map<String, Object> testLogin(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Map<String, Object> response = new HashMap<>();

        // 模拟登录验证
        if (validateCredentials(username, password)) {
            response.put("success", true);
            response.put("message", "登录成功！");
            response.put("username", username);
            response.put("role", getUserRole(username));

            // 模拟用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", "1");
            userInfo.put("username", username);
            userInfo.put("realName", getRealName(username));
            response.put("user", userInfo);
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        }

        return response;
    }

    @GetMapping("/accounts")
    public Map<String, Object> getTestAccounts() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("accounts", new String[]{
            "admin/admin123 - 系统管理员",
            "librarian/admin123 - 图书管理员",
            "student1/123456 - 学生用户"
        });
        return response;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登录服务正常运行");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    // 简单的凭据验证
    private boolean validateCredentials(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        // 模拟的验证逻辑
        switch (username) {
            case "admin":
                return "admin123".equals(password);
            case "librarian":
                return "admin123".equals(password);
            case "student1":
                return "123456".equals(password);
            default:
                return false;
        }
    }

    // 获取用户角色
    private String getUserRole(String username) {
        switch (username) {
            case "admin":
                return "ADMIN";
            case "librarian":
                return "LIBRARIAN";
            case "student1":
                return "USER";
            default:
                return "USER";
        }
    }

    // 获取真实姓名
    private String getRealName(String username) {
        switch (username) {
            case "admin":
                return "系统管理员";
            case "librarian":
                return "图书管理员";
            case "student1":
                return "张三";
            default:
                return "未知用户";
        }
    }
}