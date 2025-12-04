package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Admin;

public interface AdminService extends IService<Admin> {

    Admin findByUsername(String username);

    boolean updateLastLoginTime(Long adminId);
}