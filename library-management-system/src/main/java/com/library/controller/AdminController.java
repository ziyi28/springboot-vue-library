package com.library.controller;

import com.library.model.Admin;
import com.library.service.AdminService;
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

/**
 * 管理员管理控制器
 */
@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 获取所有管理员（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Admin>>> getAllAdmins(HttpSession session) {

        // 检查用户是否已登录且有管理员权限
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            List<Admin> admins = adminService.getAllAdmins();
            return ResponseEntity.ok(ApiResponse.success("获取管理员列表成功", admins));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取管理员列表失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取管理员
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Admin>> getAdminById(@PathVariable Long id, HttpSession session) {
        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            Optional<Admin> admin = adminService.getAdminById(id);
            if (admin.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("获取管理员成功", admin.get()));
            } else {
                return ResponseEntity.status(404)
                    .body(ApiResponse.notFound("管理员不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取管理员失败: " + e.getMessage()));
        }
    }

    /**
     * 创建管理员
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Admin>> createAdmin(
            @RequestBody Admin admin,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            // 验证必填字段
            if (admin.getUsername() == null || admin.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("用户名不能为空"));
            }

            if (admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("密码不能为空"));
            }

            if (admin.getRealName() == null || admin.getRealName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("真实姓名不能为空"));
            }

            // 检查用户名是否已存在
            if (adminService.existsByUsername(admin.getUsername())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("用户名已存在"));
            }

            // 检查邮箱是否已存在
            if (admin.getEmail() != null && adminService.existsByEmail(admin.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("邮箱已存在"));
            }

            // 检查员工编号是否已存在
            if (admin.getEmployeeId() != null && adminService.existsByEmployeeId(admin.getEmployeeId())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("员工编号已存在"));
            }

            Admin savedAdmin = adminService.createAdmin(admin);
            return ResponseEntity.ok(ApiResponse.success("创建管理员成功", savedAdmin));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("创建管理员失败: " + e.getMessage()));
        }
    }

    /**
     * 更新管理员
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Admin>> updateAdmin(
            @PathVariable Long id,
            @RequestBody Admin admin,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            Optional<Admin> existingAdminOpt = adminService.getAdminById(id);
            if (!existingAdminOpt.isPresent()) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.notFound("管理员不存在"));
            }

            Admin existingAdmin = existingAdminOpt.get();

            // 验证必填字段
            if (admin.getUsername() == null || admin.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("用户名不能为空"));
            }

            if (admin.getRealName() == null || admin.getRealName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("真实姓名不能为空"));
            }

            // 检查用户名是否已被其他管理员使用
            if (!existingAdmin.getUsername().equals(admin.getUsername()) &&
                adminService.existsByUsername(admin.getUsername())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("用户名已存在"));
            }

            // 检查邮箱是否已被其他管理员使用
            if (admin.getEmail() != null && !admin.getEmail().equals(existingAdmin.getEmail()) &&
                adminService.existsByEmail(admin.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("邮箱已存在"));
            }

            // 检查员工编号是否已被其他管理员使用
            if (admin.getEmployeeId() != null && !admin.getEmployeeId().equals(existingAdmin.getEmployeeId()) &&
                adminService.existsByEmployeeId(admin.getEmployeeId())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("员工编号已存在"));
            }

            // 更新字段
            existingAdmin.setUsername(admin.getUsername());
            existingAdmin.setRealName(admin.getRealName());
            existingAdmin.setEmail(admin.getEmail());
            existingAdmin.setEmployeeId(admin.getEmployeeId());
            existingAdmin.setDepartment(admin.getDepartment());
            existingAdmin.setRole(admin.getRole());
            existingAdmin.setStatus(admin.getStatus());

            // 只有提供了新密码才更新密码
            if (admin.getPassword() != null && !admin.getPassword().trim().isEmpty()) {
                existingAdmin.setPassword(admin.getPassword());
            }

            Admin updatedAdmin = adminService.updateAdmin(id, existingAdmin);
            return ResponseEntity.ok(ApiResponse.success("更新管理员成功", updatedAdmin));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("更新管理员失败: " + e.getMessage()));
        }
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAdmin(
            @PathVariable Long id,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            Optional<Admin> adminOpt = adminService.getAdminById(id);
            if (!adminOpt.isPresent()) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.notFound("管理员不存在"));
            }

            adminService.deleteAdmin(id);
            return ResponseEntity.ok(ApiResponse.success("删除管理员成功", "管理员已删除"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("删除管理员失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户名查找管理员
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<Admin>> getAdminByUsername(
            @PathVariable String username,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            Optional<Admin> admin = adminService.getAdminByUsername(username);
            if (admin.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("获取管理员成功", admin.get()));
            } else {
                return ResponseEntity.status(404)
                    .body(ApiResponse.notFound("管理员不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取管理员失败: " + e.getMessage()));
        }
    }

    /**
     * 搜索管理员
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Admin>>> searchAdmins(
            @RequestParam String keyword,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            List<Admin> admins = adminService.searchAdmins(keyword);
            return ResponseEntity.ok(ApiResponse.success("搜索管理员成功", admins));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("搜索管理员失败: " + e.getMessage()));
        }
    }

    /**
     * 获取管理员统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getAdminStats(HttpSession session) {
        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            Object stats = adminService.getAdminStats();
            return ResponseEntity.ok(ApiResponse.success("获取管理员统计成功", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取管理员统计失败: " + e.getMessage()));
        }
    }

    /**
     * 更新管理员登录时间
     */
    @PostMapping("/{id}/login")
    public ResponseEntity<ApiResponse<Admin>> updateLoginTime(
            @PathVariable Long id,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            Optional<Admin> adminOpt = adminService.getAdminById(id);
            if (!adminOpt.isPresent()) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.notFound("管理员不存在"));
            }

            adminService.updateLastLoginTime(id);
            Optional<Admin> updatedAdmin = adminService.getAdminById(id);

            return ResponseEntity.ok(ApiResponse.success("更新登录时间成功", updatedAdmin.get()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("更新登录时间失败: " + e.getMessage()));
        }
    }
}