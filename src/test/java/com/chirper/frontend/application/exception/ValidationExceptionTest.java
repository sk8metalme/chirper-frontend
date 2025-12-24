package com.chirper.frontend.application.exception;

import com.chirper.frontend.domain.valueobject.FieldError;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationExceptionTest {

    @Test
    void shouldCreateValidationException() {
        // Arrange
        List<FieldError> errors = List.of(
                new FieldError("username", "ユーザー名は必須です"),
                new FieldError("password", "パスワードは必須です")
        );

        // Act
        ValidationException exception = new ValidationException(errors);

        // Assert
        assertEquals(2, exception.getErrors().size());
        assertEquals("username", exception.getErrors().get(0).getField());
        assertEquals("ユーザー名は必須です", exception.getErrors().get(0).getMessage());
        assertNotNull(exception.getMessage());
    }

    @Test
    void shouldCreateValidationExceptionWithMessage() {
        // Arrange
        String message = "Validation failed";
        List<FieldError> errors = List.of(
                new FieldError("email", "メールアドレスの形式が正しくありません")
        );

        // Act
        ValidationException exception = new ValidationException(message, errors);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(1, exception.getErrors().size());
    }
}
