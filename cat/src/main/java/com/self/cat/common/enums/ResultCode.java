package com.self.cat.common.enums;

public enum ResultCode {

    // Success codes
    SUCCESS(200, "Operation successful"),

    // Client error codes (4xx)
    PARAM_ERROR(400, "Parameter validation failed"),
    UNAUTHORIZED(401, "Unauthorized access"),
    FORBIDDEN(403, "Access denied"),
    NOT_FOUND(404, "Resource not found"),

    // Business error codes (custom)
    USER_ALREADY_EXISTS(1001, "User already exists"),
    PASSWORD_ERROR(1002, "Incorrect password"),
    CONFIRM_ERROR(1003, "确认密码和密码不一致"),

    // Server error codes (5xx)
    ERROR(500, "Internal server error"),

    // Pet error codes (custom)
    SAVE_PET_ERROR(2001,"保存失败"),
    DELETE_PER_ERROR(2002,"删除失败"),

    // User error codes
    LOGIN_USER(3001,"账号或密码错误");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}