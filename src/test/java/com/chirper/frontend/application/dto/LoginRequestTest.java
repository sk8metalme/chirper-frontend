package com.chirper.frontend.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void shouldCreateLoginRequest() {
        // Arrange
        String username = "testuser";
        String password = "password123";

        // Act
        LoginRequest request = new LoginRequest(username, password);

        // Assert
        assertEquals(username, request.username());
        assertEquals(password, request.password());
    }

    @Test
    void shouldBeImmutable() {
        // Arrange & Act
        LoginRequest request = new LoginRequest("testuser", "password123");

        // Assert
        assertNotNull(request);
        assertInstanceOf(LoginRequest.class, request);
    }
}
