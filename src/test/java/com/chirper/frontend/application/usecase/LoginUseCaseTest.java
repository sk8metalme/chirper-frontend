package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.dto.LoginResponse;
import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.application.exception.ValidationException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.domain.service.IClientValidationService;
import com.chirper.frontend.domain.valueobject.FieldError;
import com.chirper.frontend.domain.valueobject.ValidationResult;
import com.chirper.frontend.infrastructure.exception.BackendApiException;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private IBackendApiRepository apiRepository;

    @Mock
    private IClientValidationService validationService;

    @Mock
    private JwtSessionManager sessionManager;

    @Mock
    private HttpServletRequest request;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(apiRepository, validationService, sessionManager);
    }

    @Test
    void shouldLoginSuccessfully() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String jwtToken = "jwt-token-123";
        String userId = "user-123";

        when(validationService.validateLoginForm(username, password))
                .thenReturn(ValidationResult.valid());
        when(apiRepository.login(username, password))
                .thenReturn(new LoginResponse(jwtToken, userId));

        // Act
        LoginResponse response = loginUseCase.execute(request, username, password);

        // Assert
        assertNotNull(response);
        assertEquals(jwtToken, response.jwtToken());
        assertEquals(userId, response.userId());
        verify(sessionManager).saveJwtToken(request, jwtToken, userId, username);
    }

    @Test
    void shouldFailWhenValidationFails() {
        // Arrange
        String username = "u";
        String password = "pwd";
        List<FieldError> errors = List.of(
                new FieldError("username", "ユーザー名は3-20文字である必要があります"),
                new FieldError("password", "パスワードは8文字以上である必要があります")
        );

        when(validationService.validateLoginForm(username, password))
                .thenReturn(ValidationResult.invalid(errors));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () ->
                loginUseCase.execute(request, username, password)
        );

        assertEquals(2, exception.getErrors().size());
        verify(apiRepository, never()).login(any(), any());
        verify(sessionManager, never()).saveJwtToken(any(), any(), any(), any());
    }

    @Test
    void shouldFailWhenBackendApiReturnsUnauthorized() {
        // Arrange
        String username = "testuser";
        String password = "wrongpassword";

        when(validationService.validateLoginForm(username, password))
                .thenReturn(ValidationResult.valid());
        when(apiRepository.login(username, password))
                .thenThrow(new UnauthorizedException("ユーザー名またはパスワードが正しくありません"));

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                loginUseCase.execute(request, username, password)
        );

        assertEquals("ユーザー名またはパスワードが正しくありません", exception.getMessage());
        verify(sessionManager, never()).saveJwtToken(any(), any(), any(), any());
    }

    @Test
    void shouldFailWhenBackendApiThrowsException() {
        // Arrange
        String username = "testuser";
        String password = "password123";

        when(validationService.validateLoginForm(username, password))
                .thenReturn(ValidationResult.valid());
        when(apiRepository.login(username, password))
                .thenThrow(new BackendApiException("Server error", 500));

        // Act & Assert
        BackendApiException exception = assertThrows(BackendApiException.class, () ->
                loginUseCase.execute(request, username, password)
        );

        assertEquals("Server error", exception.getMessage());
        verify(sessionManager, never()).saveJwtToken(any(), any(), any(), any());
    }
}
