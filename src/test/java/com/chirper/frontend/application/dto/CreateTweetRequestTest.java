package com.chirper.frontend.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateTweetRequestTest {

    @Test
    void shouldCreateTweetRequest() {
        // Arrange
        String content = "Hello, World!";

        // Act
        CreateTweetRequest request = new CreateTweetRequest(content);

        // Assert
        assertEquals(content, request.content());
    }

    @Test
    void shouldHandleMaxLengthContent() {
        // Arrange
        String content = "a".repeat(280);

        // Act
        CreateTweetRequest request = new CreateTweetRequest(content);

        // Assert
        assertEquals(content, request.content());
        assertEquals(280, request.content().length());
    }
}
