package com.chirper.frontend.infrastructure.client;

import com.chirper.frontend.application.dto.FollowListDto;
import com.chirper.frontend.application.dto.LoginResponse;
import com.chirper.frontend.application.dto.RegisterResponse;
import com.chirper.frontend.application.dto.TimelineDto;
import com.chirper.frontend.application.dto.UserProfileDto;
import com.chirper.frontend.infrastructure.exception.BackendApiException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BackendApiClient tests with mockWebServer.
 *
 * Note: This test class uses SocketPolicy.DISCONNECT_AT_START to simulate network errors.
 * To prevent interference between tests when running in parallel, this class is configured
 * to run tests sequentially using @Execution(ExecutionMode.SAME_THREAD).
 */
@Execution(ExecutionMode.SAME_THREAD)
class BackendApiClientTest {

    private MockWebServer mockWebServer;
    private BackendApiClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        client = new BackendApiClient(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldLoginSuccessfully() throws InterruptedException {
        // Given
        String username = "testuser";
        String password = "password123";
        String responseJson = "{\"jwtToken\":\"test-token\",\"userId\":\"user123\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json"));

        // When
        LoginResponse response = client.login(username, password);

        // Then
        assertNotNull(response);
        assertEquals("test-token", response.jwtToken());
        assertEquals("user123", response.userId());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/auth/login", request.getPath());
        assertEquals("POST", request.getMethod());
        assertTrue(request.getBody().readUtf8().contains("\"username\":\"testuser\""));
    }

    @Test
    void shouldThrowExceptionOnLoginFailure() {
        // Given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\":\"INVALID_CREDENTIALS\"}"));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.login("user", "wrong"));

        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void shouldRegisterSuccessfully() throws InterruptedException {
        // Given
        String responseJson = "{\"userId\":\"new-user\",\"message\":\"登録が完了しました\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json"));

        // When
        RegisterResponse response = client.register("newuser", "new@example.com", "password");

        // Then
        assertNotNull(response);
        assertEquals("new-user", response.userId());
        assertEquals("登録が完了しました", response.message());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/auth/register", request.getPath());
        assertEquals("POST", request.getMethod());
    }

    @Test
    void shouldGetTimelineSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String responseJson = "{\"tweets\":[],\"currentPage\":0,\"totalPages\":1,\"totalElements\":0}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json"));

        // When
        TimelineDto response = client.getTimeline(jwtToken, 0, 20);

        // Then
        assertNotNull(response);

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/timeline?page=0&size=20", request.getPath());
        assertEquals("Bearer valid-token", request.getHeader("Authorization"));
    }

    @Test
    void shouldGetUserProfileSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String userId = "user123";
        String responseJson = "{\"userId\":\"user123\",\"username\":\"testuser\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json"));

        // When
        UserProfileDto response = client.getUserProfile(jwtToken, userId);

        // Then
        assertNotNull(response);

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/users/user123", request.getPath());
        assertEquals("Bearer valid-token", request.getHeader("Authorization"));
    }

    @Test
    void shouldCreateTweetSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String content = "Hello, Chirper!";
        String responseJson = "{\"tweetId\":\"tweet123\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json"));

        // When
        client.createTweet(jwtToken, content);

        // Then
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/tweets", request.getPath());
        assertEquals("POST", request.getMethod());
        assertEquals("Bearer valid-token", request.getHeader("Authorization"));
        assertTrue(request.getBody().readUtf8().contains("\"content\":\"Hello, Chirper!\""));
    }

    @Test
    void shouldFollowUserSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String userId = "user456";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        // When
        client.followUser(jwtToken, userId);

        // Then
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/users/user456/follow", request.getPath());
        assertEquals("POST", request.getMethod());
        assertEquals("Bearer valid-token", request.getHeader("Authorization"));
    }

    @Test
    void shouldUnfollowUserSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String userId = "user456";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        // When
        client.unfollowUser(jwtToken, userId);

        // Then
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/users/user456/follow", request.getPath());
        assertEquals("DELETE", request.getMethod());
        assertEquals("Bearer valid-token", request.getHeader("Authorization"));
    }

    @Test
    void shouldHandleNetworkError() {
        // Given
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START));

        // When & Then
        assertThrows(BackendApiException.class,
                () -> client.login("user", "pass"));
    }

    @Test
    void shouldLikeTweetSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        // When
        client.likeTweet(jwtToken, tweetId);

        // Then
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/tweets/tweet123/like", request.getPath());
        assertEquals("POST", request.getMethod());
        assertEquals("Bearer valid-token", request.getHeader("Authorization"));
    }

    @Test
    void shouldUnlikeTweetSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        // When
        client.unlikeTweet(jwtToken, tweetId);

        // Then
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/tweets/tweet123/like", request.getPath());
        assertEquals("DELETE", request.getMethod());
        assertEquals("Bearer valid-token", request.getHeader("Authorization"));
    }

    @Test
    void shouldRetweetTweetSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        // When
        client.retweetTweet(jwtToken, tweetId);

        // Then
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/tweets/tweet123/retweet", request.getPath());
        assertEquals("POST", request.getMethod());
        assertEquals("Bearer valid-token", request.getHeader("Authorization"));
    }

    @Test
    void shouldUnretweetTweetSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        // When
        client.unretweetTweet(jwtToken, tweetId);

        // Then
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/tweets/tweet123/retweet", request.getPath());
        assertEquals("DELETE", request.getMethod());
        assertEquals("Bearer valid-token", request.getHeader("Authorization"));
    }

    @Test
    void shouldDeleteTweetSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        // When
        client.deleteTweet(jwtToken, tweetId);

        // Then
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/tweets/tweet123", request.getPath());
        assertEquals("DELETE", request.getMethod());
        assertEquals("Bearer valid-token", request.getHeader("Authorization"));
    }

    @Test
    void shouldUpdateProfileSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String displayName = "Updated Name";
        String bio = "Updated bio";
        String avatarUrl = "https://example.com/avatar.jpg";
        String responseJson = "{\"userId\":\"user123\",\"username\":\"testuser\",\"displayName\":\"Updated Name\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json"));

        // When
        UserProfileDto response = client.updateProfile(jwtToken, displayName, bio, avatarUrl);

        // Then
        assertNotNull(response);
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/users/profile", request.getPath());
        assertEquals("PUT", request.getMethod());
        assertEquals("Bearer valid-token", request.getHeader("Authorization"));
        String requestBody = request.getBody().readUtf8();
        assertTrue(requestBody.contains("\"displayName\":\"Updated Name\""));
        assertTrue(requestBody.contains("\"bio\":\"Updated bio\""));
    }

    @Test
    void shouldHandleNullValuesInUpdateProfile() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String responseJson = "{\"userId\":\"user123\",\"username\":\"testuser\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json"));

        // When
        UserProfileDto response = client.updateProfile(jwtToken, null, null, null);

        // Then
        assertNotNull(response);
        RecordedRequest request = mockWebServer.takeRequest();
        String requestBody = request.getBody().readUtf8();
        assertTrue(requestBody.contains("\"displayName\":\"\""));
        assertTrue(requestBody.contains("\"bio\":\"\""));
        assertTrue(requestBody.contains("\"avatarUrl\":\"\""));
    }

    @Test
    void shouldThrowBackendApiExceptionOnInvalidJsonInRegister() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        String invalidJson = "invalid-json-response";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(invalidJson)
                .addHeader("Content-Type", "application/json"));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.register(username, email, password));
        assertEquals("新規登録中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldThrowBackendApiExceptionOnInvalidJsonInGetTimeline() {
        // Given
        String jwtToken = "valid-token";
        int page = 0;
        int size = 20;
        String invalidJson = "invalid-json-response";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(invalidJson)
                .addHeader("Content-Type", "application/json"));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.getTimeline(jwtToken, page, size));
        assertEquals("タイムライン取得中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldThrowBackendApiExceptionOnInvalidJsonInCreateTweet() {
        // Given
        String jwtToken = "valid-token";
        String content = "Hello, world!";
        String invalidJson = "invalid-json-response";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(invalidJson)
                .addHeader("Content-Type", "application/json"));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.createTweet(jwtToken, content));
        assertEquals("ツイート作成中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldThrowBackendApiExceptionOnInvalidJsonInGetUserProfile() {
        // Given
        String jwtToken = "valid-token";
        String username = "testuser";
        String invalidJson = "invalid-json-response";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(invalidJson)
                .addHeader("Content-Type", "application/json"));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.getUserProfile(jwtToken, username));
        assertEquals("ユーザープロフィール取得中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldThrowBackendApiExceptionOnInvalidJsonInUpdateProfile() {
        // Given
        String jwtToken = "valid-token";
        String displayName = "Updated Name";
        String bio = "Updated bio";
        String avatarUrl = "https://example.com/avatar.jpg";
        String invalidJson = "invalid-json-response";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(invalidJson)
                .addHeader("Content-Type", "application/json"));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.updateProfile(jwtToken, displayName, bio, avatarUrl));
        assertEquals("プロフィール更新中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldThrowBackendApiExceptionOnNetworkErrorInFollowUser() {
        // Given
        String jwtToken = "valid-token";
        String userId = "user456";
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.followUser(jwtToken, userId));
        assertEquals("フォロー中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldThrowBackendApiExceptionOnNetworkErrorInUnfollowUser() {
        // Given
        String jwtToken = "valid-token";
        String userId = "user456";
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.unfollowUser(jwtToken, userId));
        assertEquals("アンフォロー中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldThrowBackendApiExceptionOnNetworkErrorInLikeTweet() {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.likeTweet(jwtToken, tweetId));
        assertEquals("いいね中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldThrowBackendApiExceptionOnNetworkErrorInUnlikeTweet() {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.unlikeTweet(jwtToken, tweetId));
        assertEquals("いいね解除中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldThrowBackendApiExceptionOnNetworkErrorInRetweetTweet() {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.retweetTweet(jwtToken, tweetId));
        assertEquals("リツイート中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldThrowBackendApiExceptionOnNetworkErrorInUnretweetTweet() {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.unretweetTweet(jwtToken, tweetId));
        assertEquals("リツイート解除中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldThrowBackendApiExceptionOnNetworkErrorInDeleteTweet() {
        // Given
        String jwtToken = "valid-token";
        String tweetId = "tweet123";
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.deleteTweet(jwtToken, tweetId));
        assertEquals("ツイート削除中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldGetFollowersSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String username = "testuser";
        int page = 0;
        int size = 100;
        String responseJson = "{\"users\":[],\"currentPage\":0,\"totalPages\":1,\"totalItems\":0}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json"));

        // When
        FollowListDto result = client.getFollowers(jwtToken, username, page, size);

        // Then
        assertNotNull(result);
        assertEquals(0, result.currentPage());
        assertEquals(1, result.totalPages());
        assertEquals(0, result.totalItems());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertTrue(request.getPath().contains("/users/" + username + "/followers"));
        assertTrue(request.getPath().contains("page=" + page));
        assertTrue(request.getPath().contains("size=" + size));
        assertEquals("Bearer " + jwtToken, request.getHeader("Authorization"));
    }

    @Test
    void shouldGetFollowingSuccessfully() throws InterruptedException {
        // Given
        String jwtToken = "valid-token";
        String username = "testuser";
        int page = 0;
        int size = 100;
        String responseJson = "{\"users\":[],\"currentPage\":0,\"totalPages\":1,\"totalItems\":0}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json"));

        // When
        FollowListDto result = client.getFollowing(jwtToken, username, page, size);

        // Then
        assertNotNull(result);
        assertEquals(0, result.currentPage());
        assertEquals(1, result.totalPages());
        assertEquals(0, result.totalItems());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertTrue(request.getPath().contains("/users/" + username + "/following"));
        assertTrue(request.getPath().contains("page=" + page));
        assertTrue(request.getPath().contains("size=" + size));
        assertEquals("Bearer " + jwtToken, request.getHeader("Authorization"));
    }

    @Test
    void shouldThrowBackendApiExceptionOnNetworkErrorInGetFollowers() {
        // Given
        String jwtToken = "valid-token";
        String username = "testuser";
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.getFollowers(jwtToken, username, 0, 100));
        assertEquals("フォロワー一覧取得中にエラーが発生しました", exception.getMessage());
    }

    @Test
    void shouldThrowBackendApiExceptionOnNetworkErrorInGetFollowing() {
        // Given
        String jwtToken = "valid-token";
        String username = "testuser";
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        // When & Then
        BackendApiException exception = assertThrows(BackendApiException.class,
                () -> client.getFollowing(jwtToken, username, 0, 100));
        assertEquals("フォロー中一覧取得中にエラーが発生しました", exception.getMessage());
    }
}
