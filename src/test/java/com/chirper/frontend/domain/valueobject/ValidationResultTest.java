package com.chirper.frontend.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationResultTest {

    @Test
    void valid_エラーがない場合はValidationResultを作成できる() {
        // Act
        ValidationResult result = ValidationResult.valid();

        // Assert
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void invalid_エラーがある場合はValidationResultを作成できる() {
        // Arrange
        FieldError error1 = new FieldError("username", "ユーザー名は必須です");
        FieldError error2 = new FieldError("password", "パスワードは8文字以上必要です");
        List<FieldError> errors = List.of(error1, error2);

        // Act
        ValidationResult result = ValidationResult.invalid(errors);

        // Assert
        assertFalse(result.isValid());
        assertEquals(2, result.getErrors().size());
        assertEquals("username", result.getErrors().get(0).getField());
        assertEquals("ユーザー名は必須です", result.getErrors().get(0).getMessage());
    }

    @Test
    void invalid_nullのエラーリストの場合は例外をスローする() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            ValidationResult.invalid(null);
        });
    }
}
