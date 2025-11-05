package com.miniwallet.exception;

public class CustomException extends RuntimeException {
    private String errorCode;

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}