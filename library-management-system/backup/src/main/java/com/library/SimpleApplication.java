package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleApplication.class, args);
    }

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