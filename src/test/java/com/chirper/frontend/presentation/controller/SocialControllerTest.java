package com.chirper.frontend.presentation.controller;

import com.chirper.frontend.application.usecase.FollowUserUseCase;
import com.chirper.frontend.application.usecase.UnfollowUserUseCase;
import com.chirper.frontend.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SocialController のテスト
 */
@WebMvcTest(SocialController.class)
@Import(SecurityConfig.class)
class SocialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FollowUserUseCase followUserUseCase;

    @MockBean
    private UnfollowUserUseCase unfollowUserUseCase;

    @Test
    void shouldFollowUserSuccessfully() throws Exception {
        // Arrange
        doNothing().when(followUserUseCase).execute(any(), eq("user-123"));

        // Act & Assert
        mockMvc.perform(post("/follow/user-123")
                        .with(csrf())
                        .with(user("testuser"))
                        .header("Referer", "http://localhost/profile/otheruser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/otheruser"))
                .andExpect(flash().attribute("success", "フォローしました"));

        verify(followUserUseCase).execute(any(), eq("user-123"));
    }

    @Test
    void shouldRedirectToTimelineWhenNoReferer() throws Exception {
        // Arrange
        doNothing().when(followUserUseCase).execute(any(), eq("user-123"));

        // Act & Assert
        mockMvc.perform(post("/follow/user-123")
                        .with(csrf())
                        .with(user("testuser")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"))
                .andExpect(flash().attribute("success", "フォローしました"));
    }

    @Test
    void shouldHandleFollowFailure() throws Exception {
        // Arrange
        doThrow(new RuntimeException("フォローに失敗しました"))
                .when(followUserUseCase).execute(any(), eq("user-123"));

        // Act & Assert
        mockMvc.perform(post("/follow/user-123")
                        .with(csrf())
                        .with(user("testuser"))
                        .header("Referer", "http://localhost/timeline"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"))
                .andExpect(flash().attribute("error", "フォローに失敗しました"));
    }

    @Test
    void shouldUnfollowUserSuccessfully() throws Exception {
        // Arrange
        doNothing().when(unfollowUserUseCase).execute(any(), eq("user-123"));

        // Act & Assert
        mockMvc.perform(post("/unfollow/user-123")
                        .with(csrf())
                        .with(user("testuser"))
                        .header("Referer", "http://localhost/profile/otheruser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/otheruser"))
                .andExpect(flash().attribute("success", "フォローを解除しました"));

        verify(unfollowUserUseCase).execute(any(), eq("user-123"));
    }

    @Test
    void shouldDisplayFollowersList() throws Exception {
        mockMvc.perform(get("/followers/testuser")
                        .with(user("viewer")))
                .andExpect(status().isOk())
                .andExpect(view().name("followers"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attributeExists("followers"));
    }

    @Test
    void shouldDisplayFollowingList() throws Exception {
        mockMvc.perform(get("/following/testuser")
                        .with(user("viewer")))
                .andExpect(status().isOk())
                .andExpect(view().name("following"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attributeExists("following"));
    }
}
