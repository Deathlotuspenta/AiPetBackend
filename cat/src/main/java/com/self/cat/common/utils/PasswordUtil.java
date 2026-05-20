package com.self.cat.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    // Strength (log rounds) ranges from 4 to 31. Default is 10.
    // 强度（迭代次数）范围为 4 到 31。默认为 10。
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    /**
     * Hash the password using BCrypt
     * 使用 BCrypt 对密码进行哈希
     */
    public static String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    /**
     * Verify if the plain password matches the stored hash
     * 验证明文密码是否与存储的哈希值匹配
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
