package com.chirper.frontend.infrastructure.service;

import com.chirper.frontend.domain.valueobject.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientValidationServiceTest {

    private ClientValidationService service;

    @BeforeEach
    void setUp() {
        service = new ClientValidationService();
    }

    // Login Form Validation Tests
    @Test
    void shouldValidateValidLoginForm() {
        // When
        ValidationResult result = service.validateLoginForm("validuser", "password123");

        // Then
        assertTrue(result.isValid());
    }

    @Test
    void shouldRejectLoginFormWithEmptyUsername() {
        // When
        ValidationResult result = service.validateLoginForm("", "password123");

        // Then
        assertFalse(result.isValid());
    }

    @Test
    void shouldRejectLoginFormWithShortPassword() {
        // When
        ValidationResult result = service.validateLoginForm("validuser", "short");

        // Then
        assertFalse(result.isValid());
    }

    // Registration Form Validation Tests
    @Test
    void shouldValidateValidRegistrationForm() {
        // When
        ValidationResult result = service.validateRegistrationForm(
                "validuser", "test@example.com", "password123", "password123");

        // Then
        assertTrue(result.isValid());
    }

    @Test
    void shouldRejectRegistrationFormWithMismatchedPasswords() {
        // When
        ValidationResult result = service.validateRegistrationForm(
                "validuser", "test@example.com", "password123", "different");

        // Then
        assertFalse(result.isValid());
    }

    @Test
    void shouldRejectRegistrationFormWithInvalidEmail() {
        // When
        ValidationResult result = service.validateRegistrationForm(
                "validuser", "invalid-email", "password123", "password123");

        // Then
        assertFalse(result.isValid());
    }

    @Test
    void shouldRejectRegistrationFormWithShortUsername() {
        // When
        ValidationResult result = service.validateRegistrationForm(
                "ab", "test@example.com", "password123", "password123");

        // Then
        assertFalse(result.isValid());
    }

    // Tweet Form Validation Tests
    @Test
    void shouldValidateValidTweetForm() {
        // When
        ValidationResult result = service.validateTweetForm("Hello, World!");

        // Then
        assertTrue(result.isValid());
    }

    @Test
    void shouldRejectEmptyTweetForm() {
        // When
        ValidationResult result = service.validateTweetForm("");

        // Then
        assertFalse(result.isValid());
    }

    @Test
    void shouldRejectTooLongTweetForm() {
        // Given
        String longContent = "a".repeat(281);

        // When
        ValidationResult result = service.validateTweetForm(longContent);

        // Then
        assertFalse(result.isValid());
    }

    @Test
    void shouldAcceptMaxLengthTweetForm() {
        // Given
        String maxContent = "a".repeat(280);

        // When
        ValidationResult result = service.validateTweetForm(maxContent);

        // Then
        assertTrue(result.isValid());
    }

    // Profile Edit Form Validation Tests
    @Test
    void shouldValidateValidProfileEditForm() {
        // When
        ValidationResult result = service.validateProfileEditForm(
                "John Doe", "Software Engineer", "https://example.com/avatar.jpg");

        // Then
        assertTrue(result.isValid());
    }

    @Test
    void shouldRejectProfileEditFormWithTooLongDisplayName() {
        // Given
        String longName = "a".repeat(51);

        // When
        ValidationResult result = service.validateProfileEditForm(
                longName, "Bio", "https://example.com/avatar.jpg");

        // Then
        assertFalse(result.isValid());
    }

    @Test
    void shouldRejectProfileEditFormWithTooLongBio() {
        // Given
        String longBio = "a".repeat(161);

        // When
        ValidationResult result = service.validateProfileEditForm(
                "John Doe", longBio, "https://example.com/avatar.jpg");

        // Then
        assertFalse(result.isValid());
    }

    @Test
    void shouldRejectProfileEditFormWithInvalidAvatarUrl() {
        // When
        ValidationResult result = service.validateProfileEditForm(
                "John Doe", "Bio", "not-a-url");

        // Then
        assertFalse(result.isValid());
    }

    @Test
    void shouldAcceptProfileEditFormWithNullFields() {
        // When
        ValidationResult result = service.validateProfileEditForm(null, null, null);

        // Then
        assertTrue(result.isValid());
    }
}
