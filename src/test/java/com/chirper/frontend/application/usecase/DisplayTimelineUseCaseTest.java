package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.dto.TimelineDto;
import com.chirper.frontend.application.dto.TweetDto;
import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.domain.model.TimelineViewModel;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.domain.service.ITimelineFormattingService;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisplayTimelineUseCaseTest {

    @Mock
    private IBackendApiRepository apiRepository;

    @Mock
    private ITimelineFormattingService formattingService;

    @Mock
    private JwtSessionManager sessionManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private TimelineViewModel mockViewModel;

    private DisplayTimelineUseCase displayTimelineUseCase;

    @BeforeEach
    void setUp() {
        displayTimelineUseCase = new DisplayTimelineUseCase(
                apiRepository,
                formattingService,
                sessionManager
        );
    }

    @Test
    void shouldDisplayTimelineSuccessfully() {
        // Arrange
        String jwtToken = "jwt-token-123";
        int page = 0;
        int size = 20;
        TimelineDto timelineDto = new TimelineDto(
                List.of(new TweetDto("1", "user1", "testuser", "Hello!", Instant.now(), 0, 0, false, false)),
                page,
                1,
                1L
        );

        when(sessionManager.getJwtToken(request)).thenReturn(jwtToken);
        when(apiRepository.getTimeline(jwtToken, page, size)).thenReturn(timelineDto);
        when(formattingService.formatTimeline(anyList(), eq(page), eq(1))).thenReturn(mockViewModel);

        // Act
        TimelineViewModel result = displayTimelineUseCase.execute(request, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(mockViewModel, result);
        verify(sessionManager).getJwtToken(request);
        verify(apiRepository).getTimeline(jwtToken, page, size);
    }

    @Test
    void shouldFailWhenNotAuthenticated() {
        // Arrange
        when(sessionManager.getJwtToken(request)).thenReturn(null);

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                displayTimelineUseCase.execute(request, 0, 20)
        );

        assertEquals("ログインが必要です", exception.getMessage());
        verify(apiRepository, never()).getTimeline(any(), anyInt(), anyInt());
    }
}
