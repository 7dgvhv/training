package com.dorm.utils;

import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern STUDENT_PATTERN = Pattern.compile("^(3125|3225)\\d{6,}$");
    private static final Pattern ADMIN_PATTERN = Pattern.compile("^0025\\d{4,}$");

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