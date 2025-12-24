package com.chirper.frontend.infrastructure.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BackendApiExceptionTest {

    @Test
    void shouldCreateExceptionWithStatusCode() {
        // Given
        String message = "API Error";
        int statusCode = 404;

        // When
        BackendApiException exception = new BackendApiException(message, statusCode);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(statusCode, exception.getStatusCode());
        assertNull(exception.getErrorCode());
    }

    @Test
    void shouldCreateExceptionWithStatusCodeAndErrorCode() {
        // Given
        String message = "API Error";
        int statusCode = 400;
        String errorCode = "INVALID_REQUEST";

        // When
        BackendApiException exception = new BackendApiException(message, statusCode, errorCode);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(errorCode, exception.getErrorCode());
    }

    @Test
    void shouldCreateExceptionWithCause() {
        // Given
        String message = "Connection Error";
        Throwable cause = new RuntimeException("Network timeout");

        // When
        BackendApiException exception = new BackendApiException(message, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(500, exception.getStatusCode());
        assertNull(exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }
}
