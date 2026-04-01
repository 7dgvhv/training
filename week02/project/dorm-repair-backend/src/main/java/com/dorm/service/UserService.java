package com.dorm.service;

import com.dorm.entity.User;

public interface UserService {
    User register(String username, String password, String name, Integer role);
    User login(String username, String password);
    boolean bindDorm(Long userId, String building, String room);
    User getUserById(Long userId);
    boolean changePassword(Long userId, String oldPwd, String newPwd);
    boolean existsByUsername(String username);
}