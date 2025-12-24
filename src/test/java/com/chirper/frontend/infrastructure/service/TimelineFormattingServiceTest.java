package com.chirper.frontend.infrastructure.service;

import com.chirper.frontend.application.dto.TweetDto;
import com.chirper.frontend.domain.model.TimelineViewModel;
import com.chirper.frontend.domain.model.TweetViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimelineFormattingServiceTest {

    private TimelineFormattingService service;

    @BeforeEach
    void setUp() {
        service = new TimelineFormattingService();
    }

    @Test
    void shouldFormatEmptyTimeline() {
        // Given
        List<TweetDto> tweets = Collections.emptyList();
        int currentPage = 0;
        int totalPages = 1;

        // When
        TimelineViewModel result = service.formatTimeline(tweets, currentPage, totalPages);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTweets().size());
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void shouldFormatTimelineWithTweets() {
        // Given
        TweetDto tweetDto = new TweetDto(
                "tweet1",
                "user456",
                "testuser",
                "Hello, World!",
                Instant.now(),
                10,
                5,
                false,
                false
        );
        List<TweetDto> tweets = List.of(tweetDto);

        // When
        TimelineViewModel result = service.formatTimeline(tweets, 0, 1);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTweets().size());

        TweetViewModel tweet = result.getTweets().get(0);
        assertEquals("tweet1", tweet.getTweetId());
        assertEquals("user456", tweet.getUserId());
        assertEquals("testuser", tweet.getUsername());
        assertNotNull(tweet.getContent());
        assertEquals(10, tweet.getLikesCount());
        assertEquals(5, tweet.getRetweetsCount());
    }

    @Test
    void shouldPreservePaginationInfo() {
        // Given
        List<TweetDto> tweets = Collections.emptyList();

        // When
        TimelineViewModel result = service.formatTimeline(tweets, 2, 5);

        // Then
        assertEquals(2, result.getCurrentPage());
        assertEquals(5, result.getTotalPages());
        assertTrue(result.hasNextPage());
    }
}
