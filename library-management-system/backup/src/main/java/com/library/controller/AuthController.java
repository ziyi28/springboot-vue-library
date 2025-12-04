package com.library.controller;

import com.library.common.Result;
import com.library.entity.Admin;
import com.library.entity.User;
import com.library.service.AdminService;
import com.library.service.UserService;
import com.library.utils.JwtUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private AdminService adminService;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
        private String type; // user 或 admin
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String type = loginRequest.getType();

        if ("user".equals(type)) {
            User user = userService.findByUsername(username);
            if (user != null && passwordEncoder.matches(password, user.getPassword())) {
                if (user.getStatus() != 1) {
                    return Result.error("账户已被禁用");
                }

                String token = jwtUtils.generateToken(username, "USER");
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("user", user);
                data.put("role", "USER");
                return Result.success(data);
            }
        } else if ("admin".equals(type)) {
            Admin admin = adminService.findByUsername(username);
            if (admin != null && passwordEncoder.matches(password, admin.getPassword())) {
                if (admin.getStatus() != 1) {
                    return Result.error("账户已被禁用");
                }

                String token = jwtUtils.generateToken(username, admin.getRole());
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("admin", admin);
                data.put("role", admin.getRole());

                // 更新最后登录时间
                adminService.updateLastLoginTime(admin.getId());

                return Result.success(data);
            }
        }

        return Result.error("用户名或密码错误");
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        boolean success = userService.register(user);
        return success ? Result.success("注册成功") : Result.error("注册失败，用户名已存在");
    }
}