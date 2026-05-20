package com.self.cat.common.exception;

public class UserException extends RuntimeException {
    private final int code;
    public UserException(final int code, final String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
