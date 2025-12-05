package com.library.service.impl;

import com.library.model.Admin;
import com.library.model.Admin.AdminRole;
import com.library.repository.AdminRepository;
import com.library.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Admin createAdmin(Admin admin) {
        // 检查用户名、邮箱、员工号是否已存在
        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (admin.getEmail() != null && adminRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        if (admin.getEmployeeId() != null && adminRepository.existsByEmployeeId(admin.getEmployeeId())) {
            throw new RuntimeException("员工号已存在");
        }

        // 加密密码
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Admin updateAdmin(Long id, Admin admin) {
        Optional<Admin> existingAdmin = adminRepository.findById(id);
        if (existingAdmin.isEmpty()) {
            throw new RuntimeException("管理员不存在");
        }

        Admin updatedAdmin = existingAdmin.get();

        // 检查用户名、邮箱、员工号是否与其他管理员冲突
        if (!updatedAdmin.getUsername().equals(admin.getUsername()) &&
            adminRepository.existsByUsername(admin.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        if (admin.getEmail() != null && !admin.getEmail().equals(updatedAdmin.getEmail()) &&
            adminRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        if (admin.getEmployeeId() != null && !admin.getEmployeeId().equals(updatedAdmin.getEmployeeId()) &&
            adminRepository.existsByEmployeeId(admin.getEmployeeId())) {
            throw new RuntimeException("员工号已存在");
        }

        // 更新基本信息
        updatedAdmin.setUsername(admin.getUsername());
        updatedAdmin.setEmail(admin.getEmail());
        updatedAdmin.setRealName(admin.getRealName());
        updatedAdmin.setEmployeeId(admin.getEmployeeId());
        updatedAdmin.setDepartment(admin.getDepartment());
        updatedAdmin.setRole(admin.getRole());
        updatedAdmin.setStatus(admin.getStatus());

        // 如果提供了新密码，则加密并更新
        if (admin.getPassword() != null && !admin.getPassword().trim().isEmpty()) {
            updatedAdmin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }

        return adminRepository.save(updatedAdmin);
    }

    @Override
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new RuntimeException("管理员不存在");
        }
        adminRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Admin> authenticate(String username, String password) {
        Optional<Admin> admin = adminRepository.findByUsername(username);
        if (admin.isPresent() && admin.get().getStatus() &&
            passwordEncoder.matches(password, admin.get().getPassword())) {
            return admin;
        }
        return Optional.empty();
    }

    @Override
    public void updateLastLoginTime(Long adminId) {
        Optional<Admin> admin = adminRepository.findById(adminId);
        if (admin.isPresent()) {
            admin.get().setLastLoginTime(LocalDateTime.now());
            adminRepository.save(admin.get());
        }
    }

    @Override
    public boolean changePassword(Long adminId, String oldPassword, String newPassword) {
        Optional<Admin> admin = adminRepository.findById(adminId);
        if (admin.isEmpty()) {
            return false;
        }

        Admin existingAdmin = admin.get();
        if (!passwordEncoder.matches(oldPassword, existingAdmin.getPassword())) {
            return false;
        }

        existingAdmin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(existingAdmin);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Admin> getAdminByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Admin> getAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Admin> getAdminByEmployeeId(String employeeId) {
        return adminRepository.findByEmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> getAdminsByRole(AdminRole role) {
        return adminRepository.findByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> getActiveAdmins() {
        return adminRepository.findByStatusTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> getAdminsByDepartment(String department) {
        return adminRepository.findByDepartmentContainingIgnoreCase(department);
    }

    @Override
    public void enableAdmin(Long adminId) {
        Optional<Admin> admin = adminRepository.findById(adminId);
        if (admin.isPresent()) {
            admin.get().setStatus(true);
            adminRepository.save(admin.get());
        }
    }

    @Override
    public void disableAdmin(Long adminId) {
        Optional<Admin> admin = adminRepository.findById(adminId);
        if (admin.isPresent()) {
            admin.get().setStatus(false);
            adminRepository.save(admin.get());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> getDisabledAdmins() {
        return adminRepository.findByStatus(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> searchAdmins(String keyword) {
        return adminRepository.searchAdmins(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> getRecentLogins() {
        return adminRepository.findRecentLogins();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> getLoginsSince(LocalDateTime since) {
        return adminRepository.findLoginsSince(since);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalAdminCount() {
        return adminRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveAdminCount() {
        return adminRepository.findByStatusTrue().size();
    }

    @Override
    @Transactional(readOnly = true)
    public long getAdminCountByRole(AdminRole role) {
        return adminRepository.findByRole(role).size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAdminCountByRole() {
        return adminRepository.countByRole();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAdminCountByStatus() {
        return adminRepository.countByStatus();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return adminRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmployeeId(String employeeId) {
        return adminRepository.existsByEmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !adminRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !adminRepository.existsByEmail(email);
    }

    @Override
    public Admin createSystemAdmin(String username, String password, String email, String realName) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        admin.setEmail(email);
        admin.setRealName(realName);
        admin.setRole(AdminRole.ADMIN);
        admin.setStatus(true);

        return createAdmin(admin);
    }

    @Override
    public Admin createLibrarian(String username, String password, String email, String realName, String department) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        admin.setEmail(email);
        admin.setRealName(realName);
        admin.setDepartment(department);
        admin.setRole(AdminRole.LIBRARIAN);
        admin.setStatus(true);

        return createAdmin(admin);
    }

    @Override
    public void resetPassword(Long adminId, String newPassword) {
        Optional<Admin> admin = adminRepository.findById(adminId);
        if (admin.isPresent()) {
            admin.get().setPassword(passwordEncoder.encode(newPassword));
            adminRepository.save(admin.get());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> getAdminsByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return adminRepository.findAll(pageable).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> getAdminsByRoleAndStatus(AdminRole role, Boolean status) {
        return adminRepository.findByRoleAndStatus(role, status);
    }
}