package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnfollowUserUseCaseTest {

    @Mock
    private IBackendApiRepository apiRepository;

    @Mock
    private JwtSessionManager sessionManager;

    @Mock
    private HttpServletRequest request;

    private UnfollowUserUseCase unfollowUserUseCase;

    @BeforeEach
    void setUp() {
        unfollowUserUseCase = new UnfollowUserUseCase(apiRepository, sessionManager);
    }

    @Test
    void shouldUnfollowUserSuccessfully() {
        // Arrange
        String userId = "user123";
        String jwtToken = "valid-token";

        when(sessionManager.getJwtToken(request)).thenReturn(jwtToken);

        // Act
        unfollowUserUseCase.execute(request, userId);

        // Assert
        verify(apiRepository).unfollowUser(jwtToken, userId);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowUnauthorizedExceptionForInvalidToken(String token) {
        // Arrange
        String userId = "user123";

        when(sessionManager.getJwtToken(request)).thenReturn(token);

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> unfollowUserUseCase.execute(request, userId));

        assertEquals("ログインが必要です", exception.getMessage());
        verify(apiRepository, never()).unfollowUser(any(), any());
    }
}
