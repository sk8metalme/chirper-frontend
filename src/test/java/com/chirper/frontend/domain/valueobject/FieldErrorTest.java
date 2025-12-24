package com.chirper.frontend.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldErrorTest {

    @Test
    void constructor_有効なフィールド名とメッセージでインスタンスを作成できる() {
        // Act
        FieldError error = new FieldError("username", "ユーザー名は必須です");

        // Assert
        assertEquals("username", error.getField());
        assertEquals("ユーザー名は必須です", error.getMessage());
    }

    @Test
    void constructor_フィールド名がnullの場合は例外をスローする() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new FieldError(null, "メッセージ");
        });
    }

    @Test
    void constructor_メッセージがnullの場合は例外をスローする() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new FieldError("field", null);
        });
    }
}
