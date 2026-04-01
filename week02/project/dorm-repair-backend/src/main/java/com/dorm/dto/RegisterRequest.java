package com.dorm.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String name;
    private Integer role;   // 1-学生，2-管理员
}