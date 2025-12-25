package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowUserUseCaseTest {

    @Mock
    private IBackendApiRepository apiRepository;

    @Mock
    private JwtSessionManager sessionManager;

    @Mock
    private HttpServletRequest request;

    private FollowUserUseCase followUserUseCase;

    @BeforeEach
    void setUp() {
        followUserUseCase = new FollowUserUseCase(apiRepository, sessionManager);
    }

    @Test
    void shouldFollowUserSuccessfully() {
        // Arrange
        String userId = "user123";
        String jwtToken = "valid-token";

        when(sessionManager.getJwtToken(request)).thenReturn(jwtToken);

        // Act
        followUserUseCase.execute(request, userId);

        // Assert
        verify(apiRepository).followUser(jwtToken, userId);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenTokenIsNull() {
        // Arrange
        String userId = "user123";

        when(sessionManager.getJwtToken(request)).thenReturn(null);

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> followUserUseCase.execute(request, userId));

        assertEquals("ログインが必要です", exception.getMessage());
        verify(apiRepository, never()).followUser(any(), any());
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenTokenIsBlank() {
        // Arrange
        String userId = "user123";

        when(sessionManager.getJwtToken(request)).thenReturn("");

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> followUserUseCase.execute(request, userId));

        assertEquals("ログインが必要です", exception.getMessage());
        verify(apiRepository, never()).followUser(any(), any());
    }
}
