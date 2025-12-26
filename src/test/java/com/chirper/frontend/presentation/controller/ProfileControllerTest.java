package com.chirper.frontend.presentation.controller;

import com.chirper.frontend.application.dto.UserProfileDto;
import com.chirper.frontend.application.usecase.DisplayUserProfileUseCase;
import com.chirper.frontend.application.usecase.UpdateProfileUseCase;
import com.chirper.frontend.infrastructure.config.SecurityConfig;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProfileController のテスト
 */
@WebMvcTest(ProfileController.class)
@Import(SecurityConfig.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DisplayUserProfileUseCase displayUserProfileUseCase;

    @MockBean
    private UpdateProfileUseCase updateProfileUseCase;

    @MockBean
    private JwtSessionManager sessionManager;

    @Test
    void shouldDisplayUserProfile() throws Exception {
        // Arrange
        UserProfileDto profile = new UserProfileDto(
                "user-123", "testuser", "test@example.com",
                "Test Bio", 10, 5, false
        );
        when(displayUserProfileUseCase.execute("testuser"))
                .thenReturn(profile);
        when(sessionManager.getUsername(any())).thenReturn("viewer");

        // Act & Assert
        mockMvc.perform(get("/profile/testuser")
                        .with(user("viewer")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attribute("isOwner", false));

        verify(displayUserProfileUseCase).execute("testuser");
    }

    @Test
    void shouldDisplayUserProfileWithIsOwnerTrueForOwner() throws Exception {
        // Arrange
        UserProfileDto profile = new UserProfileDto(
                "user-123", "testuser", "test@example.com",
                "Test Bio", 10, 5, false
        );
        when(displayUserProfileUseCase.execute("testuser"))
                .thenReturn(profile);
        when(sessionManager.getUsername(any())).thenReturn("testuser");

        // Act & Assert
        mockMvc.perform(get("/profile/testuser")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attribute("isOwner", true));

        verify(displayUserProfileUseCase).execute("testuser");
    }

    @Test
    void shouldReturnForbiddenWhenNotAuthenticated() throws Exception {
        // Act & Assert - 未ログイン状態ではプロフィールページへのアクセスは認証エラー
        mockMvc.perform(get("/profile/testuser"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDisplayProfileEditForm() throws Exception {
        // Arrange
        UserProfileDto profile = new UserProfileDto(
                "user-123", "testuser", "test@example.com",
                "Test Bio", 10, 5, false
        );
        when(sessionManager.getUsername(any())).thenReturn("testuser");
        when(displayUserProfileUseCase.execute("testuser"))
                .thenReturn(profile);

        // Act & Assert
        mockMvc.perform(get("/profile/edit")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("profileForm"));
    }

    @Test
    void shouldUpdateProfileSuccessfully() throws Exception {
        // Arrange
        UserProfileDto updatedProfile = new UserProfileDto(
                "user-123", "testuser", "test@example.com",
                "Updated Bio", 10, 5, false
        );
        when(updateProfileUseCase.execute(any(), eq("Test User"), eq("Updated Bio"), eq("")))
                .thenReturn(updatedProfile);

        // Act & Assert
        mockMvc.perform(post("/profile/edit")
                        .with(csrf())
                        .with(user("testuser"))
                        .param("displayName", "Test User")
                        .param("bio", "Updated Bio")
                        .param("avatarUrl", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/testuser"))
                .andExpect(flash().attribute("success", "プロフィールを更新しました"));

        verify(updateProfileUseCase).execute(any(), eq("Test User"), eq("Updated Bio"), eq(""));
    }

    @Test
    void shouldReturnEditFormWhenValidationFails() throws Exception {
        // Act & Assert - bioが160文字を超える
        String longBio = "a".repeat(161);
        mockMvc.perform(post("/profile/edit")
                        .with(csrf())
                        .with(user("testuser"))
                        .param("displayName", "Test")
                        .param("bio", longBio)
                        .param("avatarUrl", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("error"));
    }

    // ========== Coverage Improvement Tests ==========

    @Test
    void shouldHandleNullUsernameInEditProfileForm() throws Exception {
        // Given
        when(sessionManager.getUsername(any())).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/profile/edit")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "ログインが必要です"));

        verify(displayUserProfileUseCase, never()).execute(anyString());
    }

    @Test
    void shouldHandleExceptionInEditProfileForm() throws Exception {
        // Given
        when(sessionManager.getUsername(any())).thenReturn("testuser");
        when(displayUserProfileUseCase.execute("testuser"))
                .thenThrow(new RuntimeException("API Error"));

        // When & Then
        mockMvc.perform(get("/profile/edit")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "プロフィール情報の取得に失敗しました"));
    }

    @Test
    void shouldHandleNullBioInEditProfileForm() throws Exception {
        // Given
        when(sessionManager.getUsername(any())).thenReturn("testuser");
        UserProfileDto profileWithNullBio = new UserProfileDto(
                "user123", "testuser", "test@example.com",
                null, // bio is null
                10, 5, false
        );
        when(displayUserProfileUseCase.execute("testuser")).thenReturn(profileWithNullBio);

        // When & Then
        mockMvc.perform(get("/profile/edit")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("profileForm"));

        // Verify that empty string is used for null bio
        verify(displayUserProfileUseCase).execute("testuser");
    }

    @Test
    void shouldHandleExceptionInUpdateProfile() throws Exception {
        // Given
        when(updateProfileUseCase.execute(any(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Update failed"));

        // When & Then
        mockMvc.perform(post("/profile/edit")
                        .with(csrf())
                        .with(user("testuser"))
                        .param("displayName", "Test")
                        .param("bio", "Test bio")
                        .param("avatarUrl", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/edit"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void shouldHandleNullCurrentUsernameInProfile() throws Exception {
        // Given
        UserProfileDto profile = new UserProfileDto(
                "user123", "testuser", "test@example.com",
                "Test bio", 10, 5, false
        );
        when(displayUserProfileUseCase.execute("testuser")).thenReturn(profile);
        when(sessionManager.getUsername(any())).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/profile/testuser")
                        .with(user("anonymous")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("isOwner"))
                .andExpect(model().attribute("isOwner", false));
    }
}
