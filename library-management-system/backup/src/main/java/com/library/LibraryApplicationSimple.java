package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class LibraryApplicationSimple {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplicationSimple.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "图书管理系统正在运行！";
    }

    @GetMapping("/health")
    public String health() {
        return "系统状态：正常";
    }
}