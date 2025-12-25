package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.dto.TweetDto;
import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.application.exception.ValidationException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.domain.service.IClientValidationService;
import com.chirper.frontend.domain.valueobject.FieldError;
import com.chirper.frontend.domain.valueobject.ValidationResult;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitTweetUseCaseTest {

    @Mock
    private IBackendApiRepository apiRepository;

    @Mock
    private IClientValidationService validationService;

    @Mock
    private JwtSessionManager sessionManager;

    @Mock
    private HttpServletRequest request;

    private SubmitTweetUseCase submitTweetUseCase;

    @BeforeEach
    void setUp() {
        submitTweetUseCase = new SubmitTweetUseCase(apiRepository, validationService, sessionManager);
    }

    @Test
    void shouldSubmitTweetSuccessfully() {
        // Arrange
        String content = "Hello, world!";
        String jwtToken = "valid-token";
        TweetDto expectedTweet = new TweetDto(
                "tweet123", "user123", "testuser", content,
                Instant.now(), 0, 0, false, false
        );

        when(validationService.validateTweetForm(content)).thenReturn(ValidationResult.valid());
        when(sessionManager.getJwtToken(request)).thenReturn(jwtToken);
        when(apiRepository.createTweet(jwtToken, content)).thenReturn(expectedTweet);

        // Act
        TweetDto result = submitTweetUseCase.execute(request, content);

        // Assert
        assertNotNull(result);
        assertEquals(expectedTweet.tweetId(), result.tweetId());
        verify(apiRepository).createTweet(jwtToken, content);
    }

    @Test
    void shouldThrowValidationExceptionWhenContentIsInvalid() {
        // Arrange
        String content = "";
        List<FieldError> errors = List.of(
                new FieldError("content", "ツイート内容を入力してください")
        );

        when(validationService.validateTweetForm(content))
                .thenReturn(ValidationResult.invalid(errors));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> submitTweetUseCase.execute(request, content));

        assertEquals(1, exception.getErrors().size());
        verify(apiRepository, never()).createTweet(any(), any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowUnauthorizedExceptionForInvalidToken(String token) {
        // Arrange
        String content = "Hello, world!";

        when(validationService.validateTweetForm(content)).thenReturn(ValidationResult.valid());
        when(sessionManager.getJwtToken(request)).thenReturn(token);

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> submitTweetUseCase.execute(request, content));

        assertEquals("ログインが必要です", exception.getMessage());
        verify(apiRepository, never()).createTweet(any(), any());
    }
}
