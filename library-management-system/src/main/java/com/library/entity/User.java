package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
public class User extends BaseEntity {

    private String username;
    private String password;
    private String email;
    private String phone;
    private String realName;
    private String studentId;
    private String department;
    private String major;
    private String grade;
    private Integer status;
    private String avatar;
}