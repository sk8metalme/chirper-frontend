package com.chirper.frontend.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlTest {

    @Test
    void shouldCreateValidUrl() {
        // When
        Url url = new Url("https://example.com", 0, 19);

        // Then
        assertEquals("https://example.com", url.getUrl());
        assertEquals(0, url.getStart());
        assertEquals(19, url.getEnd());
    }

    @Test
    void shouldThrowExceptionWhenUrlIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Url(null, 0, 19));
        assertEquals("URL cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUrlIsBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Url("   ", 0, 19));
        assertEquals("URL cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Url("https://example.com", -1, 19));
        assertEquals("URL start position cannot be negative", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEndIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Url("https://example.com", 0, -1));
        assertEquals("URL end position cannot be negative", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartIsGreaterThanOrEqualToEnd() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Url("https://example.com", 19, 19));
        assertEquals("URL start position must be less than end position", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartIsGreaterThanEnd() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Url("https://example.com", 20, 19));
        assertEquals("URL start position must be less than end position", exception.getMessage());
    }
}
