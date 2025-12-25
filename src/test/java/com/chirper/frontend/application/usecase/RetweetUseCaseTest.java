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
class RetweetUseCaseTest {

    @Mock
    private IBackendApiRepository apiRepository;

    @Mock
    private JwtSessionManager sessionManager;

    @Mock
    private HttpServletRequest request;

    private RetweetUseCase retweetUseCase;

    @BeforeEach
    void setUp() {
        retweetUseCase = new RetweetUseCase(apiRepository, sessionManager);
    }

    @Test
    void shouldRetweetSuccessfully() {
        // Arrange
        String tweetId = "tweet123";
        String jwtToken = "valid-token";

        when(sessionManager.getJwtToken(request)).thenReturn(jwtToken);

        // Act
        retweetUseCase.execute(request, tweetId);

        // Assert
        verify(apiRepository).retweet(jwtToken, tweetId);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowUnauthorizedExceptionForInvalidToken(String token) {
        // Arrange
        String tweetId = "tweet123";

        when(sessionManager.getJwtToken(request)).thenReturn(token);

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> retweetUseCase.execute(request, tweetId));

        assertEquals("ログインが必要です", exception.getMessage());
        verify(apiRepository, never()).retweet(any(), any());
    }
}
