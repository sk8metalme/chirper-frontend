package com.chirper.frontend.infrastructure.repository;

import com.chirper.frontend.application.dto.LoginResponse;
import com.chirper.frontend.application.dto.RegisterResponse;
import com.chirper.frontend.application.dto.TimelineDto;
import com.chirper.frontend.application.dto.TweetDto;
import com.chirper.frontend.application.dto.UserProfileDto;
import com.chirper.frontend.infrastructure.client.BackendApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BackendApiRepositoryImplTest {

    private BackendApiRepositoryImpl repository;

    @Mock
    private BackendApiClient apiClient;

    @BeforeEach
    void setUp() {
        repository = new BackendApiRepositoryImpl(apiClient);
    }

    @Test
    void shouldDelegateLogin() {
        // Given
        String username = "testuser";
        String password = "password";
        LoginResponse expectedResponse = new LoginResponse("token", "user123");
        when(apiClient.login(username, password)).thenReturn(expectedResponse);

        // When
        LoginResponse result = repository.login(username, password);

        // Then
        assertEquals(expectedResponse, result);
        verify(apiClient).login(username, password);
    }

    @Test
    void shouldDelegateRegister() {
        // Given
        String username = "newuser";
        String email = "new@example.com";
        String password = "password";
        RegisterResponse expectedResponse = new RegisterResponse("token", "user456");
        when(apiClient.register(username, email, password)).thenReturn(expectedResponse);

        // When
        RegisterResponse result = repository.register(username, email, password);

        // Then
        assertEquals(expectedResponse, result);
        verify(apiClient).register(username, email, password);
    }

    @Test
    void shouldDelegateGetTimeline() {
        // Given
        String jwtToken = "valid-token";
        int page = 0;
        int size = 20;
        TimelineDto expectedResponse = new TimelineDto(Collections.emptyList(), 0, 1, 0);
        when(apiClient.getTimeline(jwtToken, page, size)).thenReturn(expectedResponse);

        // When
        TimelineDto result = repository.getTimeline(jwtToken, page, size);

        // Then
        assertEquals(expectedResponse, result);
        verify(apiClient).getTimeline(jwtToken, page, size);
    }

    @Test
    void shouldDelegateGetUserProfile() {
        // Given - getUserProfile now takes username only and throws UnsupportedOperationException
        String username = "testuser";

        // When & Then
        assertThrows(UnsupportedOperationException.class,
                () -> repository.getUserProfile(username));
    }

    @Test
    void shouldDelegateFollowUser() {
        // Given
        String jwtToken = "valid-token";
        String userId = "user456";

        // When
        repository.followUser(jwtToken, userId);

        // Then
        verify(apiClient).followUser(jwtToken, userId);
    }

    @Test
    void shouldDelegateLikeTweet() {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";

        // When
        repository.likeTweet(jwtToken, tweetId);

        // Then
        verify(apiClient).likeTweet(jwtToken, tweetId);
    }

    @Test
    void shouldDelegateDeleteTweet() {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";

        // When
        repository.deleteTweet(jwtToken, tweetId);

        // Then
        verify(apiClient).deleteTweet(jwtToken, tweetId);
    }

    @Test
    void shouldDelegateCreateTweet() {
        // Given
        String jwtToken = "valid-token";
        String content = "Hello, world!";
        TweetDto expectedResponse = new TweetDto(
                "tweet123", "user123", "testuser", content,
                Instant.now(), 0, 0, false, false
        );
        when(apiClient.createTweet(jwtToken, content)).thenReturn(expectedResponse);

        // When
        TweetDto result = repository.createTweet(jwtToken, content);

        // Then
        assertEquals(expectedResponse, result);
        verify(apiClient).createTweet(jwtToken, content);
    }

    @Test
    void shouldDelegateUpdateProfile() {
        // Given
        String jwtToken = "valid-token";
        String displayName = "Updated Name";
        String bio = "Updated bio";
        String avatarUrl = "https://example.com/avatar.jpg";
        UserProfileDto expectedResponse = new UserProfileDto(
                "user123", "testuser", "test@example.com",
                bio, 10, 5, false
        );
        when(apiClient.updateProfile(jwtToken, displayName, bio, avatarUrl))
                .thenReturn(expectedResponse);

        // When
        UserProfileDto result = repository.updateProfile(jwtToken, displayName, bio, avatarUrl);

        // Then
        assertEquals(expectedResponse, result);
        verify(apiClient).updateProfile(jwtToken, displayName, bio, avatarUrl);
    }

    @Test
    void shouldDelegateUnfollowUser() {
        // Given
        String jwtToken = "valid-token";
        String userId = "user456";

        // When
        repository.unfollowUser(jwtToken, userId);

        // Then
        verify(apiClient).unfollowUser(jwtToken, userId);
    }

    @Test
    void shouldDelegateUnlikeTweet() {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";

        // When
        repository.unlikeTweet(jwtToken, tweetId);

        // Then
        verify(apiClient).unlikeTweet(jwtToken, tweetId);
    }

    @Test
    void shouldDelegateRetweet() {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";

        // When
        repository.retweet(jwtToken, tweetId);

        // Then
        verify(apiClient).retweetTweet(jwtToken, tweetId);
    }

    @Test
    void shouldThrowUnsupportedOperationExceptionForGetTweet() {
        // Given
        String tweetId = "tweet123";

        // When & Then
        assertThrows(UnsupportedOperationException.class,
                () -> repository.getTweet(tweetId));
    }
}
