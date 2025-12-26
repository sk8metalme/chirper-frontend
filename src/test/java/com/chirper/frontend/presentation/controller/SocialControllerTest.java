package com.chirper.frontend.presentation.controller;

import com.chirper.frontend.application.dto.FollowListDto;
import com.chirper.frontend.application.usecase.FollowUserUseCase;
import com.chirper.frontend.application.usecase.UnfollowUserUseCase;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.infrastructure.config.SecurityConfig;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

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

    @MockBean
    private IBackendApiRepository apiRepository;

    @MockBean
    private JwtSessionManager sessionManager;

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
        // Arrange
        String jwtToken = "valid-token";
        FollowListDto followersDto = new FollowListDto(Collections.emptyList(), 0, 1, 0);
        when(sessionManager.getJwtToken(any())).thenReturn(jwtToken);
        when(apiRepository.getFollowers(eq(jwtToken), eq("testuser"), eq(0), eq(20)))
                .thenReturn(followersDto);

        // Act & Assert
        mockMvc.perform(get("/followers/testuser")
                        .with(user("viewer")))
                .andExpect(status().isOk())
                .andExpect(view().name("followers"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attributeExists("followers"))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 1));

        verify(sessionManager).getJwtToken(any());
        verify(apiRepository).getFollowers(eq(jwtToken), eq("testuser"), eq(0), eq(20));
    }

    @Test
    void shouldDisplayFollowingList() throws Exception {
        // Arrange
        String jwtToken = "valid-token";
        FollowListDto followingDto = new FollowListDto(Collections.emptyList(), 0, 1, 0);
        when(sessionManager.getJwtToken(any())).thenReturn(jwtToken);
        when(apiRepository.getFollowing(eq(jwtToken), eq("testuser"), eq(0), eq(20)))
                .thenReturn(followingDto);

        // Act & Assert
        mockMvc.perform(get("/following/testuser")
                        .with(user("viewer")))
                .andExpect(status().isOk())
                .andExpect(view().name("following"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attributeExists("following"))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 1));

        verify(sessionManager).getJwtToken(any());
        verify(apiRepository).getFollowing(eq(jwtToken), eq("testuser"), eq(0), eq(20));
    }

    @Test
    void shouldHandleNullTokenInFollowers() throws Exception {
        // Arrange
        when(sessionManager.getJwtToken(any())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/followers/testuser")
                        .with(user("viewer")))
                .andExpect(status().isOk())
                .andExpect(view().name("followers"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("followers", Collections.emptyList()));

        verify(sessionManager).getJwtToken(any());
        verify(apiRepository, never()).getFollowers(any(), any(), anyInt(), anyInt());
    }

    @Test
    void shouldHandleNullTokenInFollowing() throws Exception {
        // Arrange
        when(sessionManager.getJwtToken(any())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/following/testuser")
                        .with(user("viewer")))
                .andExpect(status().isOk())
                .andExpect(view().name("following"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("following", Collections.emptyList()));

        verify(sessionManager).getJwtToken(any());
        verify(apiRepository, never()).getFollowing(any(), any(), anyInt(), anyInt());
    }

    @Test
    void shouldHandleErrorInFollowers() throws Exception {
        // Arrange
        String jwtToken = "valid-token";
        when(sessionManager.getJwtToken(any())).thenReturn(jwtToken);
        when(apiRepository.getFollowers(eq(jwtToken), eq("testuser"), eq(0), eq(20)))
                .thenThrow(new RuntimeException("API エラー"));

        // Act & Assert
        mockMvc.perform(get("/followers/testuser")
                        .with(user("viewer")))
                .andExpect(status().isOk())
                .andExpect(view().name("followers"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("followers", Collections.emptyList()));
    }

    @Test
    void shouldHandleErrorInFollowing() throws Exception {
        // Arrange
        String jwtToken = "valid-token";
        when(sessionManager.getJwtToken(any())).thenReturn(jwtToken);
        when(apiRepository.getFollowing(eq(jwtToken), eq("testuser"), eq(0), eq(20)))
                .thenThrow(new RuntimeException("API エラー"));

        // Act & Assert
        mockMvc.perform(get("/following/testuser")
                        .with(user("viewer")))
                .andExpect(status().isOk())
                .andExpect(view().name("following"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("following", Collections.emptyList()));
    }

    // ========== Coverage Improvement Tests ==========

    @Test
    void shouldHandleErrorInUnfollowUser() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Unfollow failed"))
                .when(unfollowUserUseCase).execute(any(), anyString());

        // Act & Assert
        mockMvc.perform(post("/unfollow/user456")
                        .with(csrf())
                        .with(user("testuser"))
                        .header("Referer", "http://localhost/timeline"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void shouldHandleBlankRefererInFollowUser() throws Exception {
        // Arrange
        when(sessionManager.getJwtToken(any())).thenReturn("valid-token");

        // Act & Assert - blank referer should redirect to /timeline
        mockMvc.perform(post("/follow/user456")
                        .with(csrf())
                        .with(user("testuser"))
                        .header("Referer", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"));
    }

    @Test
    void shouldHandleNonAllowedPathInFollowUser() throws Exception {
        // Arrange
        when(sessionManager.getJwtToken(any())).thenReturn("valid-token");

        // Act & Assert - non-allowed path should redirect to /timeline
        mockMvc.perform(post("/follow/user456")
                        .with(csrf())
                        .with(user("testuser"))
                        .header("Referer", "http://localhost/admin/settings"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"));
    }

    @Test
    void shouldHandleInvalidURIInFollowUser() throws Exception {
        // Arrange
        when(sessionManager.getJwtToken(any())).thenReturn("valid-token");

        // Act & Assert - invalid URI should redirect to /timeline
        mockMvc.perform(post("/follow/user456")
                        .with(csrf())
                        .with(user("testuser"))
                        .header("Referer", "http://[invalid]"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"));
    }

    @Test
    void shouldHandleNullRefererInUnfollowUser() throws Exception {
        // Arrange
        when(sessionManager.getJwtToken(any())).thenReturn("valid-token");

        // Act & Assert - null referer should redirect to /timeline
        mockMvc.perform(post("/unfollow/user456")
                        .with(csrf())
                        .with(user("testuser")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"));
    }
}
