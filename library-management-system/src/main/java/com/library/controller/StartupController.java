package com.library.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 启动检查控制器
 * 确保应用正常启动并提供基本响应
 */
@RestController
public class StartupController {

    /**
     * 应用启动检查
     */
    @GetMapping("/startup")
    public Map<String, Object> startup() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "🚀 图书管理系统启动成功！");
        response.put("timestamp", System.currentTimeMillis());
        response.put("application", "图书管理系统");
        response.put("version", "1.0.0");
        return response;
    }

    /**
     * 错误页面处理
     */
    @GetMapping("/error")
    public Map<String, Object> handleError() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "页面访问错误");
        response.put("suggestion", "请尝试访问以下页面：");
        response.put("pages", new String[]{
            "http://localhost:8081/ - API根路径",
            "http://localhost:8081/simple-login.html - 登录页面",
            "http://localhost:8081/api/test/status - 系统状态",
            "http://localhost:8081/h2-console - 数据库控制台"
        });
        return response;
    }
}