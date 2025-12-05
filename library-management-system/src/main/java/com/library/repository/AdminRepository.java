package com.library.repository;

import com.library.model.Admin;
import com.library.model.Admin.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // 根据用户名查找管理员
    Optional<Admin> findByUsername(String username);

    // 根据邮箱查找管理员
    Optional<Admin> findByEmail(String email);

    // 根据员工号查找管理员
    Optional<Admin> findByEmployeeId(String employeeId);

    // 根据角色查找管理员
    List<Admin> findByRole(AdminRole role);

    // 根据状态查找管理员
    List<Admin> findByStatus(Boolean status);

    // 查找活跃的管理员
    List<Admin> findByStatusTrue();

    // 检查用户名是否存在
    boolean existsByUsername(String username);

    // 检查邮箱是否存在
    boolean existsByEmail(String email);

    // 检查员工号是否存在
    boolean existsByEmployeeId(String employeeId);

    // 根据角色和状态查找管理员
    List<Admin> findByRoleAndStatus(AdminRole role, Boolean status);

    // 查找最近登录的管理员
    @Query("SELECT a FROM Admin a WHERE a.lastLoginTime IS NOT NULL ORDER BY a.lastLoginTime DESC")
    List<Admin> findRecentLogins();

    // 查找指定时间之后登录的管理员
    @Query("SELECT a FROM Admin a WHERE a.lastLoginTime > :since ORDER BY a.lastLoginTime DESC")
    List<Admin> findLoginsSince(@Param("since") LocalDateTime since);

    // 统计不同角色的管理员数量
    @Query("SELECT a.role, COUNT(a) FROM Admin a GROUP BY a.role")
    List<Object[]> countByRole();

    // 统计活跃和禁用的管理员数量
    @Query("SELECT a.status, COUNT(a) FROM Admin a GROUP BY a.status")
    List<Object[]> countByStatus();

    // 根据部门查找管理员
    List<Admin> findByDepartmentContainingIgnoreCase(String department);

    // 模糊搜索管理员（用户名、真实姓名、邮箱）
    @Query("SELECT a FROM Admin a WHERE " +
           "LOWER(a.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.realName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Admin> searchAdmins(@Param("keyword") String keyword);
}