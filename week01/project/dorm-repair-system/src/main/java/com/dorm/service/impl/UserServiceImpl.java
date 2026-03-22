package com.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dorm.constant.RoleConst;
import com.dorm.entity.User;
import com.dorm.mapper.UserMapper;
import com.dorm.service.UserService;
import com.dorm.utils.DbUtils;
import com.dorm.utils.PasswordUtils;

public class UserServiceImpl implements UserService {

    @Override
    public boolean register(String username, String password, int role, String name) {
        return DbUtils.execute(session -> {
            UserMapper mapper = session.getMapper(UserMapper.class);

            // 检查账号是否已存在
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            if (mapper.selectCount(wrapper) > 0) {
                return false; // 账号已存在
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(PasswordUtils.hashPassword(password));
            user.setRole(role);
            user.setName(name);
            user.setDormBuilding(""); // 初始为空
            user.setDormRoom("");
            mapper.insert(user);
            return true;
        });
    }

    @Override
    public User login(String username, String password) {
        return DbUtils.execute(session -> {
            UserMapper mapper = session.getMapper(UserMapper.class);
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            User user = mapper.selectOne(wrapper);
            if (user != null && PasswordUtils.checkPassword(password, user.getPassword())) {
                return user;
            }
            return null;
        });
    }

    @Override
    public boolean changePassword(Long userId, String oldPwd, String newPwd) {
        return DbUtils.execute(session -> {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectById(userId);
            if (user == null) return false;
            if (!PasswordUtils.checkPassword(oldPwd, user.getPassword())) return false;
            user.setPassword(PasswordUtils.hashPassword(newPwd));
            mapper.updateById(user);
            return true;
        });
    }

    @Override
    public boolean checkOldPassword(Long userId, String oldPwd) {
        // 校验旧密码是否正确
        return DbUtils.execute(session -> {
            // 获取UserMapper
            UserMapper mapper = session.getMapper(UserMapper.class);
            // 根据用户ID查询用户
            User user = mapper.selectById(userId);
            if (user == null) {
                return false; // 用户不存在，直接返回错误
            }
            return PasswordUtils.checkPassword(oldPwd, user.getPassword());
        });
    }


    @Override
    public User getUserById(Long userId) {
        return DbUtils.execute(session -> {
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.selectById(userId);
        });
    }

    @Override
    public boolean bindDorm(Long userId, String building, String room) {
        return DbUtils.execute(session -> {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectById(userId);
            if (user == null || user.getRole() != RoleConst.STUDENT) return false;
            user.setDormBuilding(building);
            user.setDormRoom(room);
            mapper.updateById(user);
            return true;
        });
    }

    @Override
    public boolean isUsernameExists(String username) {
        return DbUtils.execute(session -> {
            UserMapper mapper = session.getMapper(UserMapper.class);
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            return mapper.selectCount(wrapper) > 0;
        });
    }
}