package com.library.controller;

import com.library.model.Admin;
import com.library.model.User;
import com.library.repository.AdminRepository;
import com.library.repository.UserRepository;
import com.library.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证控制器
 * 处理用户登录、注册等认证相关操作
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        try {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 获取用户角色
            String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

            // 生成JWT token
            String token = jwtUtil.generateToken(userDetails.getUsername(), role);

            // 获取用户信息
            Map<String, Object> userInfo = new HashMap<>();
            if ("USER".equals(role)) {
                Optional<User> userOpt = userRepository.findByUsername(username);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    userInfo.put("id", user.getId());
                    userInfo.put("username", user.getUsername());
                    userInfo.put("email", user.getEmail());
                    userInfo.put("realName", user.getRealName());
                    userInfo.put("studentId", user.getStudentId());
                    userInfo.put("department", user.getDepartment());
                    userInfo.put("major", user.getMajor());
                }
            } else {
                Optional<Admin> adminOpt = adminRepository.findByUsername(username);
                if (adminOpt.isPresent()) {
                    Admin admin = adminOpt.get();
                    userInfo.put("id", admin.getId());
                    userInfo.put("username", admin.getUsername());
                    userInfo.put("email", admin.getEmail());
                    userInfo.put("realName", admin.getRealName());
                    userInfo.put("employeeId", admin.getEmployeeId());
                    userInfo.put("department", admin.getDepartment());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("role", role);
            response.put("user", userInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查邮箱是否已存在
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "邮箱已被注册");
            return ResponseEntity.badRequest().body(response);
        }

        // 设置默认值
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.UserRole.USER);
        user.setStatus(1);

        // 保存用户
        User savedUser = userRepository.save(user);

        // 生成token
        String token = jwtUtil.generateToken(savedUser.getUsername(), "USER");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "注册成功");
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("role", "USER");
        response.put("user", savedUser);

        return ResponseEntity.ok(response);
    }

    /**
     * 验证token
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.substring(7); // 移除 "Bearer " 前缀
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            boolean isValid = jwtUtil.validateToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("valid", isValid);
            response.put("username", username);
            response.put("role", role);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("valid", false);
            response.put("message", "Token无效");

            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登出成功");

        return ResponseEntity.ok(response);
    }
}