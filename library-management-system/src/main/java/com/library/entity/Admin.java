package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admins")
public class Admin extends BaseEntity {

    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    private String role;
    private Integer status;
    private LocalDateTime lastLoginTime;
}