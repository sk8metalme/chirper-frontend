package com.chirper.frontend.application.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponse() {
        // Arrange
        String code = "VALIDATION_ERROR";
        String message = "Validation failed";
        List<ErrorDetail> details = List.of(
                new ErrorDetail("username", "Username is required")
        );
        Instant timestamp = Instant.now();

        // Act
        ErrorResponse response = new ErrorResponse(code, message, details, timestamp);

        // Assert
        assertEquals(code, response.code());
        assertEquals(message, response.message());
        assertEquals(details, response.details());
        assertEquals(timestamp, response.timestamp());
    }

    @Test
    void shouldHandleEmptyDetails() {
        // Arrange
        Instant timestamp = Instant.now();

        // Act
        ErrorResponse response = new ErrorResponse(
                "INTERNAL_ERROR",
                "Server error",
                List.of(),
                timestamp
        );

        // Assert
        assertTrue(response.details().isEmpty());
    }

    @Test
    void shouldHandleNullDetails() {
        // Arrange
        Instant timestamp = Instant.now();

        // Act
        ErrorResponse response = new ErrorResponse(
                "INTERNAL_ERROR",
                "Server error",
                null,
                timestamp
        );

        // Assert
        assertNull(response.details());
    }
}

class ErrorDetailTest {

    @Test
    void shouldCreateErrorDetail() {
        // Arrange
        String field = "username";
        String message = "Username is required";

        // Act
        ErrorDetail detail = new ErrorDetail(field, message);

        // Assert
        assertEquals(field, detail.field());
        assertEquals(message, detail.message());
    }
}
