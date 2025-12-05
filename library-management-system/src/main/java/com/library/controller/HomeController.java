package com.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 * 处理根路径访问
 */
@Controller
public class HomeController {

    /**
     * 根路径 - 重定向到登录页面
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/simple-login.html";
    }

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "redirect:/simple-login.html";
    }

    /**
     * 管理页面入口
     */
    @GetMapping("/admin")
    public String admin() {
        return "redirect:/simple-login.html";
    }
}