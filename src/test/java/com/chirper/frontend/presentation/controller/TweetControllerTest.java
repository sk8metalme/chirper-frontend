package com.chirper.frontend.presentation.controller;

import com.chirper.frontend.application.dto.TweetDto;
import com.chirper.frontend.application.usecase.DeleteTweetUseCase;
import com.chirper.frontend.application.usecase.SubmitTweetUseCase;
import com.chirper.frontend.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TweetController のテスト
 */
@WebMvcTest(TweetController.class)
@Import(SecurityConfig.class)
class TweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubmitTweetUseCase submitTweetUseCase;

    @MockBean
    private DeleteTweetUseCase deleteTweetUseCase;

    @Test
    void shouldSubmitTweetSuccessfully() throws Exception {
        // Arrange
        TweetDto tweet = mock(TweetDto.class);
        when(submitTweetUseCase.execute(any(), eq("Hello, World!")))
                .thenReturn(tweet);

        // Act & Assert
        mockMvc.perform(post("/tweets")
                        .with(csrf())
                        .with(user("testuser"))
                        .param("content", "Hello, World!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"))
                .andExpect(flash().attribute("success", "ツイートを投稿しました"));

        verify(submitTweetUseCase).execute(any(), eq("Hello, World!"));
    }

    @Test
    void shouldReturnErrorWhenTweetValidationFails() throws Exception {
        // Act & Assert - 空のcontentでバリデーションエラー
        mockMvc.perform(post("/tweets")
                        .with(csrf())
                        .with(user("testuser"))
                        .param("content", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"))
                .andExpect(flash().attribute("error", "ツイート内容に誤りがあります"))
                .andExpect(flash().attributeExists("tweetForm"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.tweetForm"));

        verify(submitTweetUseCase, never()).execute(any(), anyString());
    }

    @Test
    void shouldHandleSubmitTweetFailure() throws Exception {
        // Arrange
        when(submitTweetUseCase.execute(any(), eq("Test tweet")))
                .thenThrow(new RuntimeException("投稿に失敗しました"));

        // Act & Assert
        mockMvc.perform(post("/tweets")
                        .with(csrf())
                        .with(user("testuser"))
                        .param("content", "Test tweet"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"))
                .andExpect(flash().attribute("error", "投稿に失敗しました"));
    }

    @Test
    void shouldDeleteTweetSuccessfully() throws Exception {
        // Arrange
        doNothing().when(deleteTweetUseCase).execute(any(), eq("tweet-123"));

        // Act & Assert
        mockMvc.perform(post("/tweets/tweet-123/delete")
                        .with(csrf())
                        .with(user("testuser")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"))
                .andExpect(flash().attribute("success", "ツイートを削除しました"));

        verify(deleteTweetUseCase).execute(any(), eq("tweet-123"));
    }

    @Test
    void shouldHandleDeleteTweetFailure() throws Exception {
        // Arrange
        doThrow(new RuntimeException("削除に失敗しました"))
                .when(deleteTweetUseCase).execute(any(), eq("tweet-123"));

        // Act & Assert
        mockMvc.perform(post("/tweets/tweet-123/delete")
                        .with(csrf())
                        .with(user("testuser")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"))
                .andExpect(flash().attribute("error", "削除に失敗しました"));
    }
}
