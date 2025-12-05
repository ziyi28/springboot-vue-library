package com.library.controller;

import com.library.model.Admin;
import com.library.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 管理员控制器
 * 管理员账户管理功能
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 创建管理员
     */
    @PostMapping
    public ResponseEntity<?> createAdmin(@Valid @RequestBody Admin admin) {
        try {
            Admin createdAdmin = adminService.createAdmin(admin);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "管理员创建成功");
            response.put("data", createdAdmin);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取所有管理员
     */
    @GetMapping
    public ResponseEntity<?> getAllAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Admin> admins = adminService.getAdminsByPage(page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", admins.getContent());
        response.put("currentPage", admins.getNumber());
        response.put("totalItems", admins.getTotalElements());
        response.put("totalPages", admins.getTotalPages());
        response.put("pageSize", admins.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取管理员
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        Optional<Admin> admin = adminService.getAdminById(id);
        if (admin.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", admin.get());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "管理员不存在");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 更新管理员信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Long id, @Valid @RequestBody Admin admin) {
        try {
            Admin updatedAdmin = adminService.updateAdmin(id, admin);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "管理员信息更新成功");
            response.put("data", updatedAdmin);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        try {
            adminService.deleteAdmin(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "管理员删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 禁用管理员
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<?> disableAdmin(@PathVariable Long id) {
        adminService.disableAdmin(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "管理员已禁用");
        return ResponseEntity.ok(response);
    }

    /**
     * 启用管理员
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<?> enableAdmin(@PathVariable Long id) {
        adminService.enableAdmin(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "管理员已启用");
        return ResponseEntity.ok(response);
    }

    /**
     * 重置管理员密码
     */
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "新密码不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        adminService.resetPassword(id, newPassword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "密码重置成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 搜索管理员
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchAdmins(@RequestParam String keyword) {
        List<Admin> admins = adminService.searchAdmins(keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", admins);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取管理员统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getAdminStats() {
        long totalAdmins = adminService.getTotalAdminCount();
        long activeAdmins = adminService.getActiveAdminCount();
        long adminRoleCount = adminService.getAdminCountByRole(Admin.AdminRole.ADMIN);
        long librarianRoleCount = adminService.getAdminCountByRole(Admin.AdminRole.LIBRARIAN);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAdmins", totalAdmins);
        stats.put("activeAdmins", activeAdmins);
        stats.put("inactiveAdmins", totalAdmins - activeAdmins);
        stats.put("adminRoleCount", adminRoleCount);
        stats.put("librarianRoleCount", librarianRoleCount);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);

        return ResponseEntity.ok(response);
    }

    /**
     * 创建系统管理员
     */
    @PostMapping("/create-system-admin")
    public ResponseEntity<?> createSystemAdmin(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String email = request.get("email");
            String realName = request.get("realName");

            Admin admin = adminService.createSystemAdmin(username, password, email, realName);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "系统管理员创建成功");
            response.put("data", admin);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 创建图书管理员
     */
    @PostMapping("/create-librarian")
    public ResponseEntity<?> createLibrarian(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String email = request.get("email");
            String realName = request.get("realName");
            String department = request.get("department");

            Admin admin = adminService.createLibrarian(username, password, email, realName, department);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "图书管理员创建成功");
            response.put("data", admin);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}