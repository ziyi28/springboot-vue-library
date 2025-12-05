package com.library.service.impl;

import com.library.model.Admin;
import com.library.model.User;
import com.library.repository.AdminRepository;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 简化的用户详情服务
 * 为Spring Security提供用户认证信息
 */
// @Service  // 暂时禁用
public class SimpleUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 查找用户: " + username);

        // 先查找用户表
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && user.getStatus() == 1) {
            System.out.println("✅ 找到用户: " + username + ", 角色: " + user.getRole());
            return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true, // enabled
                true, // account non expired
                true, // credentials non expired
                true, // account non locked
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
            );
        }

        // 再查找管理员表
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin != null && admin.getStatus()) {
            System.out.println("✅ 找到管理员: " + username + ", 角色: " + admin.getRole());
            return new org.springframework.security.core.userdetails.User(
                admin.getUsername(),
                admin.getPassword(),
                true, // enabled
                true, // account non expired
                true, // credentials non expired
                true, // account non locked
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + admin.getRole()))
            );
        }

        System.out.println("❌ 用户不存在: " + username);
        throw new UsernameNotFoundException("用户不存在: " + username);
    }
}