package com.self.cat.common.http;

import java.io.Serializable;

/**
 * Standardized HTTP Response Wrapper
 * 标准化 HTTP 响应包装类
 */
public class HttpResult<T> implements Serializable {

    private int code;      // Status code (e.g., 200 for success)
    private String message; // Response message or error description
    private T data;        // The actual payload
    private long timestamp; // Time of the response

    // Private constructor to force use of static factory methods
    private HttpResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // Success static factory method
    // 成功的静态工厂方法
    public static <T> HttpResult<T> success(T data) {
        return new HttpResult<>(200, "Success", data);
    }

    // Failure static factory method
    // 失败的静态工厂方法
    public static <T> HttpResult<T> error(int code, String message) {
        return new HttpResult<>(code, message, null);
    }

    // Getters and Setters
    // Getter 和 Setter 方法
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public long getTimestamp() { return timestamp; }
}