package com.chirper.frontend.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashtagTest {

    @Test
    void shouldCreateValidHashtag() {
        // When
        Hashtag hashtag = new Hashtag("programming", 0, 12);

        // Then
        assertEquals("programming", hashtag.getTag());
        assertEquals(0, hashtag.getStart());
        assertEquals(12, hashtag.getEnd());
    }

    @Test
    void shouldThrowExceptionWhenTagIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Hashtag(null, 0, 12));
        assertEquals("Hashtag tag cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTagIsBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Hashtag("   ", 0, 12));
        assertEquals("Hashtag tag cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Hashtag("programming", -1, 12));
        assertEquals("Hashtag start position cannot be negative", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEndIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Hashtag("programming", 0, -1));
        assertEquals("Hashtag end position cannot be negative", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartIsGreaterThanOrEqualToEnd() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Hashtag("programming", 12, 12));
        assertEquals("Hashtag start position must be less than end position", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartIsGreaterThanEnd() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Hashtag("programming", 13, 12));
        assertEquals("Hashtag start position must be less than end position", exception.getMessage());
    }
}
