package com.chirper.frontend.infrastructure.exception;

/**
 * Backend API呼び出し時の例外
 */
public class BackendApiException extends RuntimeException {
    private final int statusCode;
    private final String errorCode;

    public BackendApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = null;
    }

    public BackendApiException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public BackendApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
        this.errorCode = null;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
