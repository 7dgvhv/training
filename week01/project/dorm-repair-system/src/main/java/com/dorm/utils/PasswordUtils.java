package com.dorm.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    /**
     * 加密密码
     */
    public static String hashPassword(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }

    /**
     * 验证密码
     */
    public static boolean checkPassword(String plainText, String hashed) {
        return BCrypt.checkpw(plainText, hashed);
    }
}