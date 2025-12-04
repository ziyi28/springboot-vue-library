package com.library;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "🎉 图书管理系统启动成功！";
    }

    @GetMapping("/health")
    public String health() {
        return "✅ 系统状态：运行正常";
    }

    @GetMapping("/api/test")
    public String test() {
        return "{\"message\":\"API接口正常工作\",\"status\":\"success\"}";
    }
}