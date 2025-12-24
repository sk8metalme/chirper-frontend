package com.chirper.frontend.infrastructure.client;

import com.chirper.frontend.application.dto.LoginResponse;
import com.chirper.frontend.application.dto.RegisterResponse;
import com.chirper.frontend.application.dto.TimelineDto;
import com.chirper.frontend.application.dto.UserProfileDto;
import com.chirper.frontend.infrastructure.exception.BackendApiException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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
}
