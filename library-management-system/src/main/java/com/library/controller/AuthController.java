package com.library.controller;

import com.library.model.User;
import com.library.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // 模拟用户存储（实际项目中应该使用数据库）
    private static Map<String, User> userDatabase = new HashMap<>();

    static {
        // 添加默认管理员用户
        User admin = new User("admin", "admin123", "admin@library.com");
        admin.setRealName("系统管理员");
        admin.setPassword(passwordEncoder.encode("admin123"));
        userDatabase.put("admin", admin);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User user = userDatabase.get(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(username);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", Map.of(
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "realName", user.getRealName()
            ));
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("用户名或密码错误");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        String email = registerRequest.getEmail();

        if (userDatabase.containsKey(username)) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }

        User newUser = new User(username, password, email);
        newUser.setPassword(passwordEncoder.encode(password));
        userDatabase.put(username, newUser);

        return ResponseEntity.ok("注册成功");
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}