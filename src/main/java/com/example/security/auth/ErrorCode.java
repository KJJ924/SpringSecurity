package com.example.security.auth;

public enum ErrorCode {
    EXPIRED(403, "Invalid access token"), INVALID(403, "expired token");

    private final int code;
    private final String message;


    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
