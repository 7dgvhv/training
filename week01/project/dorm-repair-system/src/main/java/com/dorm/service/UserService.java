package com.dorm.service;

import com.dorm.entity.User;

public interface UserService {
    /**
     * 注册
     * @param username 账号
     * @param password 明文密码
     * @param role 角色
     * @param name 姓名
     * @return 注册成功返回true，账号已存在返回false
     */
    boolean register(String username, String password, int role, String name);

    /**
     * 登录
     * @param username 账号
     * @param password 明文密码
     * @return 登录成功返回User对象，失败返回null
     */
    User login(String username, String password);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @return 是否成功
     */
    boolean changePassword(Long userId, String oldPwd, String newPwd);

    /**
     * 校验旧密码是否正确
     * @param userId 用户ID
     * @param oldPwd 旧密码（明文）
     * @return 旧密码正确返回true，错误返回false
     */
    boolean checkOldPassword(Long userId, String oldPwd);

    /**
     * 根据ID查询用户
     */
    User getUserById(Long userId);

    /**
     * 绑定/修改宿舍
     */
    boolean bindDorm(Long userId, String building, String room);

    /**
     * 检查用户是否存在（根据用户名）
     */
    boolean isUsernameExists(String username);
}