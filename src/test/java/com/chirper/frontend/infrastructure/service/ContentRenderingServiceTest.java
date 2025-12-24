package com.chirper.frontend.infrastructure.service;

import com.chirper.frontend.domain.valueobject.TweetContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContentRenderingServiceTest {

    private ContentRenderingService service;

    @BeforeEach
    void setUp() {
        service = new ContentRenderingService();
    }

    @Test
    void shouldRenderPlainText() {
        // Given
        TweetContent content = new TweetContent("Hello, World!");

        // When
        String result = service.renderTweetContent(content);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("Hello, World!"));
    }

    @Test
    void shouldRenderMentions() {
        // Given
        TweetContent content = new TweetContent("Hello @user123");

        // When
        String result = service.renderTweetContent(content);

        // Then
        assertTrue(result.contains("@user123"));
        assertTrue(result.contains("href="));
    }

    @Test
    void shouldRenderHashtags() {
        // Given
        TweetContent content = new TweetContent("Hello #chirper");

        // When
        String result = service.renderTweetContent(content);

        // Then
        assertTrue(result.contains("#chirper"));
        assertTrue(result.contains("class=\"hashtag\""));
    }

    @Test
    void shouldRenderUrls() {
        // Given
        TweetContent content = new TweetContent("Check out https://example.com");

        // When
        String result = service.renderTweetContent(content);

        // Then
        assertTrue(result.contains("https://example.com"));
        assertTrue(result.contains("href="));
    }

    @Test
    void shouldRenderMixedContent() {
        // Given
        TweetContent content = new TweetContent("Hey @user #test https://example.com");

        // When
        String result = service.renderTweetContent(content);

        // Then
        assertTrue(result.contains("@user"));
        assertTrue(result.contains("#test"));
        assertTrue(result.contains("https://example.com"));
    }

    @Test
    void shouldEscapeHtmlCharacters() {
        // Given
        TweetContent content = new TweetContent("<script>alert('xss')</script>");

        // When
        String result = service.renderTweetContent(content);

        // Then
        assertFalse(result.contains("<script>"));
        assertTrue(result.contains("&lt;script&gt;") || result.contains("alert"));
    }
}
