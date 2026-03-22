package com.dorm.utils;

import java.util.regex.Pattern;

public class InputValidator {

    // 学生学号正则：3125 或 3225 开头，后面至少4位数字（共10位左右）
    private static final Pattern STUDENT_PATTERN = Pattern.compile("^(3125|3225)\\d{6,}$");
    // 管理员工号正则：0025 开头，后面至少4位数字
    private static final Pattern ADMIN_PATTERN = Pattern.compile("^0025\\d{4,}$");

    /**
     * 校验账号格式
     * @param username 账号
     * @param role 角色 1-学生 2-管理员
     * @return 是否匹配
     */
    public static boolean isValidUsername(String username, int role) {
        if (username == null || username.isEmpty()) return false;
        if (role == 1) {
            return STUDENT_PATTERN.matcher(username).matches();
        } else if (role == 2) {
            return ADMIN_PATTERN.matcher(username).matches();
        }
        return false;
    }
}