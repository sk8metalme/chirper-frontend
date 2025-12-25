package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.dto.UserProfileDto;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisplayUserProfileUseCaseTest {

    @Mock
    private IBackendApiRepository apiRepository;

    private DisplayUserProfileUseCase displayUserProfileUseCase;

    @BeforeEach
    void setUp() {
        displayUserProfileUseCase = new DisplayUserProfileUseCase(apiRepository);
    }

    @Test
    void shouldDisplayUserProfileSuccessfully() {
        // Arrange
        String username = "testuser";
        UserProfileDto expectedProfile = new UserProfileDto(
                "user123", username, "test@example.com",
                "Bio text", 10, 5, false
        );

        when(apiRepository.getUserProfile(username)).thenReturn(expectedProfile);

        // Act
        UserProfileDto result = displayUserProfileUseCase.execute(username);

        // Assert
        assertNotNull(result);
        assertEquals(expectedProfile.userId(), result.userId());
        assertEquals(expectedProfile.username(), result.username());
        verify(apiRepository).getUserProfile(username);
    }
}
