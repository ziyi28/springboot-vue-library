package com.library.controller;

import com.library.model.User;
import com.library.model.Admin;
import com.library.service.UserService;
import com.library.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户认证控制器
 * 处理用户注册、登录、登出等功能
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> registerRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = registerRequest.get("username");
            String password = registerRequest.get("password");
            String email = registerRequest.get("email");
            String realName = registerRequest.get("realName");
            String studentId = registerRequest.get("studentId");
            String department = registerRequest.get("department");
            String major = registerRequest.get("major");

            // 验证必填字段
            if (username == null || password == null || email == null || realName == null) {
                response.put("success", false);
                response.put("message", "用户名、密码、邮箱和真实姓名不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查用户名是否已存在
            if (userService.existsByUsername(username)) {
                response.put("success", false);
                response.put("message", "用户名已存在");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查邮箱是否已存在
            if (userService.existsByEmail(email)) {
                response.put("success", false);
                response.put("message", "邮箱已被注册");
                return ResponseEntity.badRequest().body(response);
            }

            // 创建用户
            User user = new User();
            user.setUsername(username);
            user.setPassword(password); // 在Service层会进行加密
            user.setEmail(email);
            user.setRealName(realName);
            user.setStudentId(studentId);
            user.setDepartment(department);
            user.setMajor(major);
            user.setRole(User.UserRole.USER);
            user.setStatus(1);

            User savedUser = userService.register(user);

            response.put("success", true);
            response.put("message", "注册成功");
            response.put("user", createUserResponse(savedUser));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "注册失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 用户登录（支持管理员和普通用户）
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            if (username == null || password == null) {
                response.put("success", false);
                response.put("message", "用户名和密码不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 首先尝试管理员认证
            Optional<Admin> adminOpt = adminService.authenticate(username, password);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();

                // 检查管理员状态
                if (admin.getStatus() != null && !admin.getStatus()) {
                    response.put("success", false);
                    response.put("message", "管理员账户已被禁用");
                    return ResponseEntity.badRequest().body(response);
                }

                // 更新最后登录时间
                adminService.updateLastLoginTime(admin.getId());

                // 将管理员信息存储到session中
                session.setAttribute("admin", admin);
                session.setAttribute("userId", admin.getId());
                session.setAttribute("username", admin.getUsername());
                session.setAttribute("userRole", admin.getRole());
                session.setAttribute("userType", "admin");

                response.put("success", true);
                response.put("message", "管理员登录成功");
                response.put("user", createAdminResponse(admin));
                response.put("userType", "admin");
                response.put("sessionId", session.getId());

                return ResponseEntity.ok(response);
            }

            // 如果不是管理员，尝试普通用户认证
            Optional<User> userOpt = userService.authenticate(username, password);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // 检查用户状态
                if (user.getStatus() != null && user.getStatus() != 1) {
                    response.put("success", false);
                    response.put("message", "用户账户已被禁用");
                    return ResponseEntity.badRequest().body(response);
                }

                // 将用户信息存储到session中
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("userRole", user.getRole());
                session.setAttribute("userType", "user");

                response.put("success", true);
                response.put("message", "用户登录成功");
                response.put("user", createUserResponse(user));
                response.put("userType", "user");
                response.put("sessionId", session.getId());

                return ResponseEntity.ok(response);
            }

            // 都不是，返回登录失败
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "登录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 清除session
            session.invalidate();

            response.put("success", true);
            response.put("message", "登出成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "登出失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = (User) session.getAttribute("user");

            if (user != null) {
                response.put("success", true);
                response.put("user", createUserResponse(user));
                response.put("sessionId", session.getId());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取用户信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 检查登录状态
     */
    @GetMapping("/check-status")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
            String username = (String) session.getAttribute("username");
            String userRole = (String) session.getAttribute("userRole");

            if (isLoggedIn != null && isLoggedIn && username != null) {
                response.put("success", true);
                response.put("loggedIn", true);
                response.put("username", username);
                response.put("userRole", userRole);
            } else {
                response.put("success", true);
                response.put("loggedIn", false);
                response.put("message", "用户未登录");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "检查登录状态失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update-profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, String> updateRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            User currentUser = (User) session.getAttribute("user");

            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }

            String email = updateRequest.get("email");
            String realName = updateRequest.get("realName");
            String studentId = updateRequest.get("studentId");
            String department = updateRequest.get("department");
            String major = updateRequest.get("major");

            // 更新用户信息
            if (email != null) currentUser.setEmail(email);
            if (realName != null) currentUser.setRealName(realName);
            if (studentId != null) currentUser.setStudentId(studentId);
            if (department != null) currentUser.setDepartment(department);
            if (major != null) currentUser.setMajor(major);

            User updatedUser = userService.updateUser(currentUser);

            // 更新session中的用户信息
            session.setAttribute("user", updatedUser);

            response.put("success", true);
            response.put("message", "用户信息更新成功");
            response.put("user", createUserResponse(updatedUser));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新用户信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> passwordRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            User currentUser = (User) session.getAttribute("user");

            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }

            String oldPassword = passwordRequest.get("oldPassword");
            String newPassword = passwordRequest.get("newPassword");
            String confirmPassword = passwordRequest.get("confirmPassword");

            if (oldPassword == null || newPassword == null || confirmPassword == null) {
                response.put("success", false);
                response.put("message", "旧密码、新密码和确认密码不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (!newPassword.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "新密码和确认密码不一致");
                return ResponseEntity.badRequest().body(response);
            }

            // 验证旧密码
            if (!userService.checkPassword(currentUser, oldPassword)) {
                response.put("success", false);
                response.put("message", "旧密码不正确");
                return ResponseEntity.badRequest().body(response);
            }

            // 更新密码
            userService.changePassword(currentUser, newPassword);

            response.put("success", true);
            response.put("message", "密码修改成功");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "修改密码失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 创建用户响应对象（隐藏敏感信息）
     */
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("username", user.getUsername());
        userResponse.put("email", user.getEmail());
        userResponse.put("realName", user.getRealName());
        userResponse.put("studentId", user.getStudentId());
        userResponse.put("department", user.getDepartment());
        userResponse.put("major", user.getMajor());
        userResponse.put("role", user.getRole());
        userResponse.put("status", user.getStatus());
        userResponse.put("createTime", user.getCreateTime());
        // 不返回密码
        return userResponse;
    }

    /**
     * 创建管理员响应对象（隐藏敏感信息）
     */
    private Map<String, Object> createAdminResponse(Admin admin) {
        Map<String, Object> adminResponse = new HashMap<>();
        adminResponse.put("id", admin.getId());
        adminResponse.put("username", admin.getUsername());
        adminResponse.put("email", admin.getEmail());
        adminResponse.put("realName", admin.getRealName());
        adminResponse.put("employeeId", admin.getEmployeeId());
        adminResponse.put("department", admin.getDepartment());
        adminResponse.put("role", admin.getRole());
        adminResponse.put("status", admin.getStatus());
        adminResponse.put("createTime", admin.getCreateTime());
        adminResponse.put("lastLoginTime", admin.getLastLoginTime());
        // 不返回密码
        return adminResponse;
    }
}