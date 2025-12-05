package com.library.controller;

import com.library.model.User;
import com.library.model.Admin;
import com.library.service.UserService;
import com.library.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 检查用户是否已登录（支持管理员和普通用户）
     */
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null || session.getAttribute("admin") != null;
    }

    /**
     * 检查是否为管理员
     */
    private boolean isAdmin(HttpSession session) {
        return session.getAttribute("admin") != null;
    }

    /**
     * 获取当前登录用户的ID
     */
    private Long getCurrentUserId(HttpSession session) {
        Object userObj = session.getAttribute("user");
        Object adminObj = session.getAttribute("admin");

        if (userObj instanceof User) {
            return ((User) userObj).getId();
        } else if (adminObj instanceof Admin) {
            return ((Admin) adminObj).getId();
        }
        return null;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return ResponseEntity.ok(ApiResponse.success("用户注册成功", registeredUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 根据用户名查找用户
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<User>> findByUsername(
            @PathVariable String username,
            HttpSession session) {

        // 检查用户是否已登录
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("获取用户成功", user.get()));
        } else {
            return ResponseEntity.status(404)
                .body(ApiResponse.notFound("用户不存在"));
        }
    }

    /**
     * 根据ID查找用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> findById(@PathVariable Long id, HttpSession session) {
        // 检查用户是否已登录
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("获取用户成功", user.get()));
        } else {
            return ResponseEntity.status(404)
                .body(ApiResponse.notFound("用户不存在"));
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @RequestBody User user,
            HttpSession session) {

        // 检查用户是否已登录
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        user.setId(id);
        try {
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(ApiResponse.success("用户信息更新成功", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id, HttpSession session) {
        // 检查用户是否已登录
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("用户删除成功", "用户已删除"));
        } else {
            return ResponseEntity.status(404)
                .body(ApiResponse.notFound("用户不存在"));
        }
    }

    /**
     * 获取所有用户（分页）- 仅管理员可用
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<User>>> findAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            HttpSession session) {

        // 检查是否为管理员
        if (!isAdmin(session)) {
            return ResponseEntity.status(403)
                .body(ApiResponse.error("仅管理员可以查看用户列表"));
        }

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<User> users = userService.findAll(pageable);
            return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", users));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取用户列表失败: " + e.getMessage()));
        }
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<User>>> searchUsers(
            @RequestParam String keyword,
            HttpSession session) {

        // 检查用户是否已登录
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            List<User> users = userService.searchUsers(keyword);
            return ResponseEntity.ok(ApiResponse.success("搜索用户成功", users));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("搜索用户失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getStats(HttpSession session) {
        // 检查用户是否已登录
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            Page<User> allUsers = userService.findAll(PageRequest.of(0, 1000));
            List<User> users = allUsers.getContent();

            long activeUsers = users.stream().filter(u -> u.getStatus() != null && u.getStatus() == 1).count();
            long inactiveUsers = users.size() - activeUsers;

            Object stats = java.util.Map.of(
                "totalUsers", users.size(),
                "activeUsers", activeUsers,
                "inactiveUsers", inactiveUsers
            );

            return ResponseEntity.ok(ApiResponse.success("获取用户统计成功", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取用户统计失败: " + e.getMessage()));
        }
    }

    /**
     * 根据状态获取用户
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByStatus(
            @PathVariable Integer status,
            HttpSession session) {

        // 检查用户是否已登录
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            List<User> users = userService.findByStatus(status);
            return ResponseEntity.ok(ApiResponse.success("获取用户成功", users));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取用户失败: " + e.getMessage()));
        }
    }
}