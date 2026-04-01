package com.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dorm.entity.User;
import com.dorm.exception.BusinessException;
import com.dorm.mapper.UserMapper;
import com.dorm.service.UserService;
import com.dorm.utils.InputValidator;
import com.dorm.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User register(String username, String password, String name, Integer role) {
        if (!InputValidator.isValidUsername(username, role)) {
            throw new BusinessException(400, "账号格式不正确");
        }
        if (existsByUsername(username)) {
            throw new BusinessException(400, "账号已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setName(name);
        user.setRole(role);
        userMapper.insert(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        if (user == null || !PasswordUtil.checkPassword(password, user.getPassword())) {
            throw new BusinessException(401, "账号或密码错误");
        }
        return user;
    }

    @Override
    public boolean bindDorm(Long userId, String building, String room) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getRole() != 1) {
            throw new BusinessException(400, "用户不存在或不是学生");
        }
        user.setDormBuilding(building);
        user.setDormRoom(room);
        userMapper.updateById(user);
        return true;
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public boolean changePassword(Long userId, String oldPwd, String newPwd) {
        User user = userMapper.selectById(userId);
        if (!PasswordUtil.checkPassword(oldPwd, user.getPassword())) {
            throw new BusinessException(400, "旧密码错误");
        }
        user.setPassword(PasswordUtil.hashPassword(newPwd));
        userMapper.updateById(user);
        return true;
    }

    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }
}