package com.chirper.frontend.infrastructure.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtSessionManagerTest {

    private JwtSessionManager sessionManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        sessionManager = new JwtSessionManager();
    }

    @Test
    void shouldSaveJwtToken() {
        // Given
        when(request.getSession(true)).thenReturn(session);
        String jwtToken = "test-token";
        String userId = "user123";

        // When
        sessionManager.saveJwtToken(request, jwtToken, userId);

        // Then
        verify(session).setAttribute("JWT_TOKEN", jwtToken);
        verify(session).setAttribute("USER_ID", userId);
    }

    @Test
    void shouldGetJwtToken() {
        // Given
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("JWT_TOKEN")).thenReturn("test-token");

        // When
        String result = sessionManager.getJwtToken(request);

        // Then
        assertEquals("test-token", result);
    }

    @Test
    void shouldReturnNullWhenNoSession() {
        // Given
        when(request.getSession(false)).thenReturn(null);

        // When
        String result = sessionManager.getJwtToken(request);

        // Then
        assertNull(result);
    }

    @Test
    void shouldGetUserId() {
        // Given
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER_ID")).thenReturn("user123");

        // When
        String result = sessionManager.getUserId(request);

        // Then
        assertEquals("user123", result);
    }

    @Test
    void shouldClearSession() {
        // Given
        when(request.getSession(false)).thenReturn(session);

        // When
        sessionManager.clearSession(request);

        // Then
        verify(session).invalidate();
    }

    @Test
    void shouldNotFailWhenClearingNonExistentSession() {
        // Given
        when(request.getSession(false)).thenReturn(null);

        // When & Then (should not throw)
        assertDoesNotThrow(() -> sessionManager.clearSession(request));
    }

    @Test
    void shouldReturnTrueWhenAuthenticated() {
        // Given
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("JWT_TOKEN")).thenReturn("valid-token");

        // When
        boolean result = sessionManager.isAuthenticated(request);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenNotAuthenticated() {
        // Given
        when(request.getSession(false)).thenReturn(null);

        // When
        boolean result = sessionManager.isAuthenticated(request);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenTokenIsBlank() {
        // Given
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("JWT_TOKEN")).thenReturn("");

        // When
        boolean result = sessionManager.isAuthenticated(request);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldSaveJwtTokenWithUsername() {
        // Given
        when(request.getSession(true)).thenReturn(session);
        String jwtToken = "test-token";
        String userId = "user123";
        String username = "testuser";

        // When
        sessionManager.saveJwtToken(request, jwtToken, userId, username);

        // Then
        verify(session).setAttribute("JWT_TOKEN", jwtToken);
        verify(session).setAttribute("USER_ID", userId);
        verify(session).setAttribute("USERNAME", username);
    }

    @Test
    void shouldSaveJwtTokenWithNullUsername() {
        // Given
        when(request.getSession(true)).thenReturn(session);
        String jwtToken = "test-token";
        String userId = "user123";

        // When
        sessionManager.saveJwtToken(request, jwtToken, userId, null);

        // Then
        verify(session).setAttribute("JWT_TOKEN", jwtToken);
        verify(session).setAttribute("USER_ID", userId);
        verify(session, never()).setAttribute(eq("USERNAME"), any());
    }

    @Test
    void shouldGetUsername() {
        // Given
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("USERNAME")).thenReturn("testuser");

        // When
        String result = sessionManager.getUsername(request);

        // Then
        assertEquals("testuser", result);
    }

    @Test
    void shouldReturnNullWhenGettingUsernameWithNoSession() {
        // Given
        when(request.getSession(false)).thenReturn(null);

        // When
        String result = sessionManager.getUsername(request);

        // Then
        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenGettingUserIdWithNoSession() {
        // Given
        when(request.getSession(false)).thenReturn(null);

        // When
        String result = sessionManager.getUserId(request);

        // Then
        assertNull(result);
    }
}
