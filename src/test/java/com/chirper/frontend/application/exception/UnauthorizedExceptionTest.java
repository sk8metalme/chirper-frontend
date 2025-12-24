package com.chirper.frontend.application.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnauthorizedExceptionTest {

    @Test
    void shouldCreateUnauthorizedException() {
        // Arrange
        String message = "ユーザー名またはパスワードが正しくありません";

        // Act
        UnauthorizedException exception = new UnauthorizedException(message);

        // Assert
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldCreateUnauthorizedExceptionWithCause() {
        // Arrange
        String message = "セッションが期限切れです";
        Throwable cause = new RuntimeException("JWT token expired");

        // Act
        UnauthorizedException exception = new UnauthorizedException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
