package com.self.cat.common.exception;

public class PetException extends RuntimeException {
    private final int code;

    public PetException(final int code, final String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
