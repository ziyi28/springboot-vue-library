package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        System.out.println("🚀 正在启动图书管理系统...");
        SpringApplication.run(LibraryApplication.class, args);
    }
}