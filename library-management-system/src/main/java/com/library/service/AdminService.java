package com.library.service;

import com.library.model.Admin;
import com.library.model.Admin.AdminRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdminService {

    // 基础CRUD操作
    Admin createAdmin(Admin admin);
    Optional<Admin> getAdminById(Long id);
    List<Admin> getAllAdmins();
    Admin updateAdmin(Long id, Admin admin);
    void deleteAdmin(Long id);

    // 登录和认证相关
    Optional<Admin> authenticate(String username, String password);
    void updateLastLoginTime(Long adminId);
    boolean changePassword(Long adminId, String oldPassword, String newPassword);

    // 查询操作
    Optional<Admin> getAdminByUsername(String username);
    Optional<Admin> getAdminByEmail(String email);
    Optional<Admin> getAdminByEmployeeId(String employeeId);
    List<Admin> getAdminsByRole(AdminRole role);
    List<Admin> getActiveAdmins();
    List<Admin> getAdminsByDepartment(String department);

    // 状态管理
    void enableAdmin(Long adminId);
    void disableAdmin(Long adminId);
    List<Admin> getDisabledAdmins();

    // 搜索和筛选
    List<Admin> searchAdmins(String keyword);
    List<Admin> getRecentLogins();
    List<Admin> getLoginsSince(LocalDateTime since);

    // 统计分析
    long getTotalAdminCount();
    long getActiveAdminCount();
    long getAdminCountByRole(AdminRole role);
    List<Object[]> getAdminCountByRole();
    List<Object[]> getAdminCountByStatus();

    // 验证操作
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    boolean isUsernameAvailable(String username);
    boolean isEmailAvailable(String email);

    // 业务操作
    Admin createSystemAdmin(String username, String password, String email, String realName);
    Admin createLibrarian(String username, String password, String email, String realName, String department);
    void resetPassword(Long adminId, String newPassword);

    // 分页查询
    List<Admin> getAdminsByPage(int page, int size);
    List<Admin> getAdminsByRoleAndStatus(AdminRole role, Boolean status);

    // 统计信息
    Object getAdminStats();
}