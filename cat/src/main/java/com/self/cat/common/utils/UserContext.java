package com.self.cat.common.utils;

import java.util.Map;

public class UserContext {

    // Create a ThreadLocal to hold user data
    // 创建一个 ThreadLocal 来保存用户数据
    private static final ThreadLocal<Map<String, String>> USER_THREAD_LOCAL = new ThreadLocal<>();

    // Save data into the thread
    // 将数据存入线程
    public static void set(Map<String, String> data) {
        USER_THREAD_LOCAL.set(data);
    }

    // Get a specific value by its key
    // 通过键获取特定的值
    public static String get(String key) {
        Map<String, String> map = USER_THREAD_LOCAL.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    // Clear the data
    // 清理数据
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}