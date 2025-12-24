package com.chirper.frontend.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateProfileRequestTest {

    @Test
    void shouldCreateUpdateProfileRequest() {
        // Arrange
        String displayName = "John Doe";
        String bio = "Software developer";
        String avatarUrl = "https://example.com/avatar.jpg";

        // Act
        UpdateProfileRequest request = new UpdateProfileRequest(displayName, bio, avatarUrl);

        // Assert
        assertEquals(displayName, request.displayName());
        assertEquals(bio, request.bio());
        assertEquals(avatarUrl, request.avatarUrl());
    }

    @Test
    void shouldHandleNullValues() {
        // Act
        UpdateProfileRequest request = new UpdateProfileRequest(null, null, null);

        // Assert
        assertNull(request.displayName());
        assertNull(request.bio());
        assertNull(request.avatarUrl());
    }
}
