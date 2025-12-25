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
class LikeTweetUseCaseTest {

    @Mock
    private IBackendApiRepository apiRepository;

    @Mock
    private JwtSessionManager sessionManager;

    @Mock
    private HttpServletRequest request;

    private LikeTweetUseCase likeTweetUseCase;

    @BeforeEach
    void setUp() {
        likeTweetUseCase = new LikeTweetUseCase(apiRepository, sessionManager);
    }

    @Test
    void shouldLikeTweetSuccessfully() {
        // Arrange
        String tweetId = "tweet123";
        String jwtToken = "valid-token";

        when(sessionManager.getJwtToken(request)).thenReturn(jwtToken);

        // Act
        likeTweetUseCase.execute(request, tweetId);

        // Assert
        verify(apiRepository).likeTweet(jwtToken, tweetId);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenTokenIsNull() {
        // Arrange
        String tweetId = "tweet123";

        when(sessionManager.getJwtToken(request)).thenReturn(null);

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> likeTweetUseCase.execute(request, tweetId));

        assertEquals("ログインが必要です", exception.getMessage());
        verify(apiRepository, never()).likeTweet(any(), any());
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenTokenIsBlank() {
        // Arrange
        String tweetId = "tweet123";

        when(sessionManager.getJwtToken(request)).thenReturn("");

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> likeTweetUseCase.execute(request, tweetId));

        assertEquals("ログインが必要です", exception.getMessage());
        verify(apiRepository, never()).likeTweet(any(), any());
    }
}
