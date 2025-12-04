package com.library.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

    
    @GetMapping("/health")
    public String health() {
        return "✅ 系统状态：运行正常";
    }

    @GetMapping("/api/test")
    public String test() {
        return "{\"message\":\"API接口正常工作\",\"status\":\"success\"}";
    }

    @GetMapping("/api/status")
    public String status() {
        return "{\"message\":\"图书管理系统运行中\",\"status\":\"operational\",\"version\":\"1.0.0\"}";
    }

    }