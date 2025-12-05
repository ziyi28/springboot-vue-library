package com.library.service.impl;

import com.library.model.User;
import com.library.repository.UserRepository;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置创建时间和更新时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 设置默认状态为激活
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新允许修改的字段
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            // 检查新邮箱是否已被其他用户使用
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
            existingUser.setEmail(user.getEmail());
        }

        if (user.getRealName() != null) {
            existingUser.setRealName(user.getRealName());
        }

        if (user.getStudentId() != null) {
            existingUser.setStudentId(user.getStudentId());
        }

        if (user.getDepartment() != null) {
            existingUser.setDepartment(user.getDepartment());
        }

        if (user.getMajor() != null) {
            existingUser.setMajor(user.getMajor());
        }

        if (user.getStatus() != null) {
            existingUser.setStatus(user.getStatus());
        }

        existingUser.setUpdateTime(LocalDateTime.now());

        return userRepository.save(existingUser);
    }

    @Override
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}