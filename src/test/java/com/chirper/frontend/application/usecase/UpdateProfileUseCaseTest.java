package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.dto.UserProfileDto;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProfileUseCaseTest {

    @Mock
    private IBackendApiRepository apiRepository;

    @Mock
    private IClientValidationService validationService;

    @Mock
    private JwtSessionManager sessionManager;

    @Mock
    private HttpServletRequest request;

    private UpdateProfileUseCase updateProfileUseCase;

    @BeforeEach
    void setUp() {
        updateProfileUseCase = new UpdateProfileUseCase(apiRepository, validationService, sessionManager);
    }

    @Test
    void shouldUpdateProfileSuccessfully() {
        // Arrange
        String displayName = "Updated Name";
        String bio = "Updated bio";
        String avatarUrl = "https://example.com/avatar.jpg";
        String jwtToken = "valid-token";
        UserProfileDto expectedProfile = new UserProfileDto(
                "user123", "testuser", "test@example.com",
                bio, 10, 5, false
        );

        when(validationService.validateProfileEditForm(displayName, bio, avatarUrl))
                .thenReturn(ValidationResult.valid());
        when(sessionManager.getJwtToken(request)).thenReturn(jwtToken);
        when(apiRepository.updateProfile(jwtToken, displayName, bio, avatarUrl))
                .thenReturn(expectedProfile);

        // Act
        UserProfileDto result = updateProfileUseCase.execute(request, displayName, bio, avatarUrl);

        // Assert
        assertNotNull(result);
        assertEquals(expectedProfile.userId(), result.userId());
        assertEquals(expectedProfile.bio(), result.bio());
        verify(apiRepository).updateProfile(jwtToken, displayName, bio, avatarUrl);
    }

    @Test
    void shouldThrowValidationExceptionWhenInputIsInvalid() {
        // Arrange
        String displayName = "";
        String bio = "A".repeat(300);  // Too long
        String avatarUrl = "invalid-url";
        List<FieldError> errors = List.of(
                new FieldError("displayName", "表示名を入力してください"),
                new FieldError("bio", "自己紹介は200文字以内で入力してください")
        );

        when(validationService.validateProfileEditForm(displayName, bio, avatarUrl))
                .thenReturn(ValidationResult.invalid(errors));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> updateProfileUseCase.execute(request, displayName, bio, avatarUrl));

        assertEquals(2, exception.getErrors().size());
        verify(apiRepository, never()).updateProfile(any(), any(), any(), any());
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenTokenIsNull() {
        // Arrange
        String displayName = "Updated Name";
        String bio = "Updated bio";
        String avatarUrl = "https://example.com/avatar.jpg";

        when(validationService.validateProfileEditForm(displayName, bio, avatarUrl))
                .thenReturn(ValidationResult.valid());
        when(sessionManager.getJwtToken(request)).thenReturn(null);

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> updateProfileUseCase.execute(request, displayName, bio, avatarUrl));

        assertEquals("ログインが必要です", exception.getMessage());
        verify(apiRepository, never()).updateProfile(any(), any(), any(), any());
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenTokenIsBlank() {
        // Arrange
        String displayName = "Updated Name";
        String bio = "Updated bio";
        String avatarUrl = "https://example.com/avatar.jpg";

        when(validationService.validateProfileEditForm(displayName, bio, avatarUrl))
                .thenReturn(ValidationResult.valid());
        when(sessionManager.getJwtToken(request)).thenReturn("");

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> updateProfileUseCase.execute(request, displayName, bio, avatarUrl));

        assertEquals("ログインが必要です", exception.getMessage());
        verify(apiRepository, never()).updateProfile(any(), any(), any(), any());
    }
}
