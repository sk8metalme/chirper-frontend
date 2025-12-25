package com.chirper.frontend.presentation.controller;

import com.chirper.frontend.application.usecase.DisplayTimelineUseCase;
import com.chirper.frontend.domain.model.TimelineViewModel;
import com.chirper.frontend.domain.model.TweetViewModel;
import com.chirper.frontend.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TimelineController のテスト
 */
@WebMvcTest(TimelineController.class)
@Import(SecurityConfig.class)
class TimelineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DisplayTimelineUseCase displayTimelineUseCase;

    @Test
    void shouldDisplayTimeline() throws Exception {
        // Arrange
        TimelineViewModel timeline = new TimelineViewModel(
                List.of(), // 空のツイートリスト
                0, 1, false
        );
        when(displayTimelineUseCase.execute(any(), eq(0), eq(20)))
                .thenReturn(timeline);

        // Act & Assert
        mockMvc.perform(get("/timeline")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline"))
                .andExpect(model().attributeExists("timeline"))
                .andExpect(model().attributeExists("tweetForm"));

        verify(displayTimelineUseCase).execute(any(), eq(0), eq(20));
    }

    @Test
    void shouldDisplayTimelineWithPagination() throws Exception {
        // Arrange
        TimelineViewModel timeline = new TimelineViewModel(
                List.of(), // 空のツイートリスト
                2, 5, true
        );
        when(displayTimelineUseCase.execute(any(), eq(2), eq(10)))
                .thenReturn(timeline);

        // Act & Assert
        mockMvc.perform(get("/timeline")
                        .with(user("testuser"))
                        .param("page", "2")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("timeline"))
                .andExpect(model().attributeExists("timeline"));

        verify(displayTimelineUseCase).execute(any(), eq(2), eq(10));
    }

    @Test
    void shouldNormalizeNegativePageToZero() throws Exception {
        // Arrange
        TimelineViewModel timeline = new TimelineViewModel(
                List.of(), 0, 1, false
        );
        when(displayTimelineUseCase.execute(any(), eq(0), eq(20)))
                .thenReturn(timeline);

        // Act & Assert - 負のpageは0に正規化される
        mockMvc.perform(get("/timeline")
                        .with(user("testuser"))
                        .param("page", "-1"))
                .andExpect(status().isOk());

        verify(displayTimelineUseCase).execute(any(), eq(0), eq(20));
    }

    @Test
    void shouldNormalizeSizeTo100Maximum() throws Exception {
        // Arrange
        TimelineViewModel timeline = new TimelineViewModel(
                List.of(), 0, 1, false
        );
        when(displayTimelineUseCase.execute(any(), eq(0), eq(100)))
                .thenReturn(timeline);

        // Act & Assert - 過大なsizeは100に制限される
        mockMvc.perform(get("/timeline")
                        .with(user("testuser"))
                        .param("size", "200"))
                .andExpect(status().isOk());

        verify(displayTimelineUseCase).execute(any(), eq(0), eq(100));
    }
}
