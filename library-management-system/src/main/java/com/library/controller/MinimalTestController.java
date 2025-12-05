package com.library.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 最小化测试控制器
 * 用于测试基本的Spring Boot功能
 */
@RestController
public class MinimalTestController {

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ Spring Boot应用正常运行！");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", "OK");
        return response;
    }

  
    @GetMapping("/api")
    public Map<String, Object> api() {
        Map<String, Object> response = new HashMap<>();
        response.put("api", "图书管理系统API");
        response.put("version", "1.0.0");
        return response;
    }
}