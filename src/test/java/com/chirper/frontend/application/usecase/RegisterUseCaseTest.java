package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.dto.RegisterResponse;
import com.chirper.frontend.application.exception.ValidationException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.domain.service.IClientValidationService;
import com.chirper.frontend.domain.valueobject.FieldError;
import com.chirper.frontend.domain.valueobject.ValidationResult;
import com.chirper.frontend.infrastructure.exception.BackendApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseTest {

    @Mock
    private IBackendApiRepository apiRepository;

    @Mock
    private IClientValidationService validationService;

    private RegisterUseCase registerUseCase;

    @BeforeEach
    void setUp() {
        registerUseCase = new RegisterUseCase(apiRepository, validationService);
    }

    @Test
    void shouldRegisterSuccessfully() {
        // Arrange
        String username = "newuser";
        String email = "newuser@example.com";
        String password = "password123";
        String passwordConfirm = "password123";
        String userId = "user-456";

        when(validationService.validateRegistrationForm(username, email, password, passwordConfirm))
                .thenReturn(ValidationResult.valid());
        when(apiRepository.register(username, email, password))
                .thenReturn(new RegisterResponse(userId, "登録が完了しました"));

        // Act
        RegisterResponse response = registerUseCase.execute(username, email, password, passwordConfirm);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.userId());
        assertEquals("登録が完了しました", response.message());
    }

    @Test
    void shouldFailWhenValidationFails() {
        // Arrange
        String username = "u";
        String email = "invalid-email";
        String password = "pwd";
        String passwordConfirm = "different";
        List<FieldError> errors = List.of(
                new FieldError("username", "ユーザー名は3-20文字である必要があります"),
                new FieldError("email", "有効なメールアドレスを入力してください"),
                new FieldError("password", "パスワードは8文字以上である必要があります"),
                new FieldError("passwordConfirm", "パスワードが一致しません")
        );

        when(validationService.validateRegistrationForm(username, email, password, passwordConfirm))
                .thenReturn(ValidationResult.invalid(errors));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () ->
                registerUseCase.execute(username, email, password, passwordConfirm)
        );

        assertEquals(4, exception.getErrors().size());
        verify(apiRepository, never()).register(any(), any(), any());
    }

    @Test
    void shouldFailWhenUsernameAlreadyExists() {
        // Arrange
        String username = "existinguser";
        String email = "user@example.com";
        String password = "password123";
        String passwordConfirm = "password123";

        when(validationService.validateRegistrationForm(username, email, password, passwordConfirm))
                .thenReturn(ValidationResult.valid());
        when(apiRepository.register(username, email, password))
                .thenThrow(new BackendApiException("ユーザー名は既に使用されています", 409, "USERNAME_ALREADY_EXISTS"));

        // Act & Assert
        BackendApiException exception = assertThrows(BackendApiException.class, () ->
                registerUseCase.execute(username, email, password, passwordConfirm)
        );

        assertEquals("ユーザー名は既に使用されています", exception.getMessage());
        assertEquals(409, exception.getStatusCode());
    }
}
