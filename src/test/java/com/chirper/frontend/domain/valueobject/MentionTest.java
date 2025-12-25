package com.chirper.frontend.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MentionTest {

    @Test
    void shouldCreateValidMention() {
        // When
        Mention mention = new Mention("testuser", 0, 9);

        // Then
        assertEquals("testuser", mention.getUsername());
        assertEquals(0, mention.getStart());
        assertEquals(9, mention.getEnd());
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Mention(null, 0, 9));
        assertEquals("Mention username cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Mention("   ", 0, 9));
        assertEquals("Mention username cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Mention("testuser", -1, 9));
        assertEquals("Mention start position cannot be negative", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEndIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Mention("testuser", 0, -1));
        assertEquals("Mention end position cannot be negative", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartIsGreaterThanOrEqualToEnd() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Mention("testuser", 9, 9));
        assertEquals("Mention start position must be less than end position", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartIsGreaterThanEnd() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Mention("testuser", 10, 9));
        assertEquals("Mention start position must be less than end position", exception.getMessage());
    }
}
