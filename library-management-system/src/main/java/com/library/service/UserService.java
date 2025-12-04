package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.User;

public interface UserService extends IService<User> {

    User findByUsername(String username);

    Page<User> getUserList(Page<User> page, String keyword);

    boolean register(User user);

    boolean updatePassword(Long userId, String oldPassword, String newPassword);

    boolean updateUserStatus(Long userId, Integer status);
}