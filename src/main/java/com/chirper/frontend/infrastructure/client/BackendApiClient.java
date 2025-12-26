package com.chirper.frontend.infrastructure.client;

import com.chirper.frontend.application.dto.*;
import com.chirper.frontend.infrastructure.exception.BackendApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Backend APIクライアント
 */
@Component
public class BackendApiClient {

    /**
     * ページサイズの最大値 (DoS対策)
     */
    private static final int MAX_PAGE_SIZE = 100;

    private final WebClient webClient;

    public BackendApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * ログイン
     */
    public LoginResponse login(String username, String password) {
        try {
            return webClient.post()
                    .uri("/api/auth/login")
                    .bodyValue(Map.of(
                            "username", username,
                            "password", password
                    ))
                    .retrieve()
                    .bodyToMono(LoginResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("ログイン中にエラーが発生しました", e);
        }
    }

    /**
     * 新規登録
     */
    public RegisterResponse register(String username, String email, String password) {
        try {
            return webClient.post()
                    .uri("/api/auth/register")
                    .bodyValue(Map.of(
                            "username", username,
                            "email", email,
                            "password", password
                    ))
                    .retrieve()
                    .bodyToMono(RegisterResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("新規登録中にエラーが発生しました", e);
        }
    }

    /**
     * タイムラインを取得
     */
    public TimelineDto getTimeline(String jwtToken, int page, int size) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/timeline")
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(TimelineDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("タイムライン取得中にエラーが発生しました", e);
        }
    }

    /**
     * ユーザープロフィールを取得（userIdベース、認証あり）
     */
    public UserProfileDto getUserProfile(String jwtToken, String userId) {
        try {
            return webClient.get()
                    .uri("/api/users/{userId}", userId)
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(UserProfileDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("ユーザープロフィール取得中にエラーが発生しました", e);
        }
    }

    /**
     * ユーザープロフィールを取得（usernameベース、認証なし）
     */
    public UserProfileDto getUserProfile(String username) {
        try {
            return webClient.get()
                    .uri("/api/users/profile/{username}", username)
                    .retrieve()
                    .bodyToMono(UserProfileDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("ユーザープロフィール取得中にエラーが発生しました", e);
        }
    }

    /**
     * ツイートを作成
     */
    public TweetDto createTweet(String jwtToken, String content) {
        try {
            return webClient.post()
                    .uri("/api/tweets")
                    .header("Authorization", "Bearer " + jwtToken)
                    .bodyValue(Map.of("content", content))
                    .retrieve()
                    .bodyToMono(TweetDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("ツイート作成中にエラーが発生しました", e);
        }
    }

    /**
     * ツイートを取得
     */
    public TweetDto getTweet(String tweetId) {
        try {
            return webClient.get()
                    .uri("/api/tweets/{tweetId}", tweetId)
                    .retrieve()
                    .bodyToMono(TweetDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("ツイート取得中にエラーが発生しました", e);
        }
    }

    /**
     * ユーザーをフォロー
     */
    public void followUser(String jwtToken, String userId) {
        try {
            webClient.post()
                    .uri("/api/users/{userId}/follow", userId)
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("フォロー中にエラーが発生しました", e);
        }
    }

    /**
     * ユーザーをアンフォロー
     */
    public void unfollowUser(String jwtToken, String userId) {
        try {
            webClient.delete()
                    .uri("/api/users/{userId}/follow", userId)
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("アンフォロー中にエラーが発生しました", e);
        }
    }

    /**
     * ツイートにいいね
     */
    public void likeTweet(String jwtToken, String tweetId) {
        try {
            webClient.post()
                    .uri("/api/tweets/{tweetId}/like", tweetId)
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("いいね中にエラーが発生しました", e);
        }
    }

    /**
     * ツイートのいいねを解除
     */
    public void unlikeTweet(String jwtToken, String tweetId) {
        try {
            webClient.delete()
                    .uri("/api/tweets/{tweetId}/like", tweetId)
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("いいね解除中にエラーが発生しました", e);
        }
    }

    /**
     * ツイートをリツイート
     */
    public void retweetTweet(String jwtToken, String tweetId) {
        try {
            webClient.post()
                    .uri("/api/tweets/{tweetId}/retweet", tweetId)
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("リツイート中にエラーが発生しました", e);
        }
    }

    /**
     * リツイートを解除
     */
    public void unretweetTweet(String jwtToken, String tweetId) {
        try {
            webClient.delete()
                    .uri("/api/tweets/{tweetId}/retweet", tweetId)
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("リツイート解除中にエラーが発生しました", e);
        }
    }

    /**
     * ツイートを削除
     */
    public void deleteTweet(String jwtToken, String tweetId) {
        try {
            webClient.delete()
                    .uri("/api/tweets/{tweetId}", tweetId)
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("ツイート削除中にエラーが発生しました", e);
        }
    }

    /**
     * プロフィールを更新
     */
    public UserProfileDto updateProfile(String jwtToken, String displayName, String bio, String avatarUrl) {
        try {
            return webClient.put()
                    .uri("/api/users/profile")
                    .header("Authorization", "Bearer " + jwtToken)
                    .bodyValue(Map.of(
                            "displayName", displayName != null ? displayName : "",
                            "bio", bio != null ? bio : "",
                            "avatarUrl", avatarUrl != null ? avatarUrl : ""
                    ))
                    .retrieve()
                    .bodyToMono(UserProfileDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("プロフィール更新中にエラーが発生しました", e);
        }
    }

    /**
     * フォロワー一覧を取得
     */
    public FollowListDto getFollowers(String jwtToken, String username, int page, int size) {
        try {
            // DoS対策: size上限をMAX_PAGE_SIZEに制限
            int safeSize = Math.min(size, MAX_PAGE_SIZE);
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/users/{username}/followers")
                            .queryParam("page", page)
                            .queryParam("size", safeSize)
                            .build(username))
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(FollowListDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("フォロワー一覧取得中にエラーが発生しました", e);
        }
    }

    /**
     * フォロー中一覧を取得
     */
    public FollowListDto getFollowing(String jwtToken, String username, int page, int size) {
        try {
            // DoS対策: size上限をMAX_PAGE_SIZEに制限
            int safeSize = Math.min(size, MAX_PAGE_SIZE);
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/users/{username}/following")
                            .queryParam("page", page)
                            .queryParam("size", safeSize)
                            .build(username))
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(FollowListDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapException(e);
        } catch (Exception e) {
            throw new BackendApiException("フォロー中一覧取得中にエラーが発生しました", e);
        }
    }

    /**
     * WebClientの例外をBackendApiExceptionにマッピング
     */
    private BackendApiException mapException(WebClientResponseException e) {
        int statusCode = e.getStatusCode().value();
        String responseBody = e.getResponseBodyAsString();

        return new BackendApiException(
                "Backend APIエラー: " + e.getMessage(),
                statusCode,
                responseBody
        );
    }
}
