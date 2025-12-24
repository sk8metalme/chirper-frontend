# chirper-frontend - 設計書

## プロジェクト情報

- **プロジェクト名**: chirper-frontend
- **親プロジェクト**: chirper
- **JIRAキー**: PC
- **Confluenceスペース**: MICHI
- **作成日時**: 2025-12-23T05:54:00.000Z

## アーキテクチャ概要

chirper-frontendは**オニオンアーキテクチャ（Onion Architecture）**を採用します。これにより、Backend Serviceと統一されたアーキテクチャパターンを共有し、チーム全体での理解を深め、保守性とテスタビリティを向上させます。

### オニオンアーキテクチャの4層構造

```
┌─────────────────────────────────────────┐
│    Presentation Layer                   │  ← Controllers, Thymeleaf Views
│  ┌───────────────────────────────────┐  │
│  │   Application Layer               │  │  ← Use Cases, DTOs
│  │  ┌─────────────────────────────┐  │  │
│  │  │   Domain Layer              │  │  │  ← ViewModels, Value Objects
│  │  │   (中核 - 外部依存なし)      │  │  │
│  │  └─────────────────────────────┘  │  │
│  └───────────────────────────────────┘  │
│    Infrastructure Layer                 │  ← Backend API Client, Session
└─────────────────────────────────────────┘
```

**依存関係の方向**:
- Presentation → Application → Domain
- Infrastructure → Domain
- Domain: 他の層に依存しない（最内層）

## レイヤー設計

### 1. Domain Layer（ドメイン層）- 中核

**責務**: プレゼンテーションドメインのビジネスロジックとルール

#### 1.1 ViewModel Entities（表示用エンティティ）

##### TimelineViewModel
```java
package com.chirper.frontend.domain.model;

public class TimelineViewModel {
    private final List<TweetViewModel> tweets;
    private final int currentPage;
    private final int totalPages;
    private final boolean hasNextPage;

    // ビジネスルール: 次のページが存在するか判定
    public boolean hasNextPage() {
        return currentPage < totalPages - 1;
    }

    // ビジネスルール: タイムラインが空か判定
    public boolean isEmpty() {
        return tweets.isEmpty();
    }
}
```

##### TweetViewModel
```java
package com.chirper.frontend.domain.model;

public class TweetViewModel {
    private final String tweetId;
    private final String userId;
    private final String username;
    private final String displayName;
    private final String avatarUrl;
    private final TweetContent content;
    private final DisplayTimestamp timestamp;
    private final int likesCount;
    private final int retweetsCount;
    private final boolean likedByCurrentUser;
    private final boolean retweetedByCurrentUser;

    // ビジネスルール: ツイート削除可能か判定（投稿者のみ）
    public boolean canDelete(String currentUserId) {
        return userId.equals(currentUserId);
    }

    // ビジネスルール: いいね済みか判定
    public boolean isLiked() {
        return likedByCurrentUser;
    }

    // ビジネスルール: リツイート済みか判定
    public boolean isRetweeted() {
        return retweetedByCurrentUser;
    }
}
```

##### UserProfileViewModel
```java
package com.chirper.frontend.domain.model;

public class UserProfileViewModel {
    private final String userId;
    private final String username;
    private final String displayName;
    private final String bio;
    private final String avatarUrl;
    private final int followersCount;
    private final int followingCount;
    private final boolean followedByCurrentUser;
    private final boolean isCurrentUser;
    private final List<TweetViewModel> userTweets;

    // ビジネスルール: フォローボタン表示判定
    public boolean canFollow() {
        return !isCurrentUser;
    }

    // ビジネスルール: プロフィール編集可能か判定
    public boolean canEdit() {
        return isCurrentUser;
    }

    // ビジネスルール: フォロー状態の表示文言
    public String followButtonText() {
        return followedByCurrentUser ? "フォロー中" : "フォローする";
    }
}
```

#### 1.2 Value Objects（値オブジェクト）

##### TweetContent
```java
package com.chirper.frontend.domain.valueobject;

public class TweetContent {
    private final String rawText;
    private final List<Mention> mentions;
    private final List<Hashtag> hashtags;
    private final List<Url> urls;

    // バリデーション: 280文字制約
    public TweetContent(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("ツイート本文は必須です");
        }
        if (text.length() > 280) {
            throw new IllegalArgumentException("ツイートは280文字以内である必要があります");
        }
        this.rawText = text;
        this.mentions = extractMentions(text);
        this.hashtags = extractHashtags(text);
        this.urls = extractUrls(text);
    }

    // ビジネスルール: @mention抽出
    private List<Mention> extractMentions(String text) {
        // 正規表現: @([a-zA-Z0-9_]+)
    }

    // ビジネスルール: #hashtag抽出
    private List<Hashtag> extractHashtags(String text) {
        // 正規表現: #([a-zA-Z0-9_]+)
    }

    // ビジネスルール: URL抽出
    private List<Url> extractUrls(String text) {
        // 正規表現: https?://[^\s]+
    }

    // ビジネスルール: HTML変換（ハイライト付き）
    public String toHighlightedHtml() {
        // @mention → <a href="/profile/{username}" class="mention">@{username}</a>
        // #hashtag → <span class="hashtag">#hashtag</span>
        // URL → <a href="{url}" target="_blank" rel="noopener">{url}</a>
    }
}
```

##### DisplayTimestamp
```java
package com.chirper.frontend.domain.valueobject;

public class DisplayTimestamp {
    private final Instant timestamp;

    // ビジネスルール: 相対時刻表示
    public String toRelativeTime() {
        Duration duration = Duration.between(timestamp, Instant.now());

        if (duration.toMinutes() < 1) {
            return "たった今";
        } else if (duration.toMinutes() < 60) {
            return duration.toMinutes() + "分前";
        } else if (duration.toHours() < 24) {
            return duration.toHours() + "時間前";
        } else if (duration.toDays() < 7) {
            return duration.toDays() + "日前";
        } else {
            return formatAsDate(); // "12月23日"
        }
    }

    // ビジネスルール: 絶対時刻表示（"2025年12月23日 14:30"）
    public String toAbsoluteTime() {
        // DateTimeFormatter使用
    }
}
```

##### ValidationResult
```java
package com.chirper.frontend.domain.valueobject;

public class ValidationResult {
    private final boolean isValid;
    private final List<FieldError> errors;

    public static ValidationResult valid() {
        return new ValidationResult(true, Collections.emptyList());
    }

    public static ValidationResult invalid(List<FieldError> errors) {
        return new ValidationResult(false, errors);
    }

    public boolean isValid() {
        return isValid;
    }

    public List<FieldError> getErrors() {
        return errors;
    }
}

public class FieldError {
    private final String field;
    private final String message;
}
```

#### 1.3 Domain Service（ドメインサービス）

##### ITimelineFormattingService
```java
package com.chirper.frontend.domain.service;

public interface ITimelineFormattingService {
    // タイムラインの整形ロジック
    TimelineViewModel formatTimeline(List<TweetDto> tweets, int currentPage, int totalPages);
}
```

##### IContentRenderingService
```java
package com.chirper.frontend.domain.service;

public interface IContentRenderingService {
    // ツイート本文のHTML変換（@mention、#hashtag、URLのハイライト）
    String renderTweetContent(TweetContent content);
}
```

##### IClientValidationService
```java
package com.chirper.frontend.domain.service;

public interface IClientValidationService {
    // ログインフォームのバリデーション
    ValidationResult validateLoginForm(String username, String password);

    // 登録フォームのバリデーション
    ValidationResult validateRegistrationForm(String username, String email, String password, String passwordConfirm);

    // ツイート投稿フォームのバリデーション
    ValidationResult validateTweetForm(String content);

    // プロフィール編集フォームのバリデーション
    ValidationResult validateProfileEditForm(String displayName, String bio, String avatarUrl);
}
```

#### 1.4 Repository Interface（リポジトリインターフェース）

##### IBackendApiRepository
```java
package com.chirper.frontend.domain.repository;

public interface IBackendApiRepository {
    // 認証API
    LoginResponse login(String username, String password);
    RegisterResponse register(String username, String email, String password);

    // タイムラインAPI
    TimelineDto getTimeline(String jwtToken, int page, int size);

    // ツイートAPI
    TweetDto createTweet(String jwtToken, String content);
    TweetDto getTweet(String tweetId);
    void deleteTweet(String jwtToken, String tweetId);

    // ユーザーAPI
    UserProfileDto getUserProfile(String username);
    UserProfileDto updateProfile(String jwtToken, String displayName, String bio, String avatarUrl);

    // ソーシャルAPI
    void followUser(String jwtToken, String userId);
    void unfollowUser(String jwtToken, String userId);
    void likeTweet(String jwtToken, String tweetId);
    void unlikeTweet(String jwtToken, String tweetId);
    void retweet(String jwtToken, String tweetId);
}
```

### 2. Application Layer（アプリケーション層）

**責務**: 画面表示・操作のユースケース実装

#### 2.1 Use Cases（ユースケース）

##### DisplayTimelineUseCase
```java
package com.chirper.frontend.application.usecase;

@Service
public class DisplayTimelineUseCase {
    private final IBackendApiRepository apiRepository;
    private final ITimelineFormattingService formattingService;
    private final SessionStorageService sessionStorage;

    public TimelineViewModel execute(int page, int size) {
        // 1. JWTトークン取得
        String jwtToken = sessionStorage.getJwtToken()
            .orElseThrow(() -> new UnauthorizedException("ログインが必要です"));

        // 2. Backend APIからタイムライン取得
        TimelineDto timelineDto = apiRepository.getTimeline(jwtToken, page, size);

        // 3. ドメインサービスでViewModelに変換
        return formattingService.formatTimeline(
            timelineDto.getTweets(),
            timelineDto.getCurrentPage(),
            timelineDto.getTotalPages()
        );
    }
}
```

##### SubmitTweetUseCase
```java
package com.chirper.frontend.application.usecase;

@Service
public class SubmitTweetUseCase {
    private final IBackendApiRepository apiRepository;
    private final IClientValidationService validationService;
    private final SessionStorageService sessionStorage;

    public TweetViewModel execute(String content) {
        // 1. クライアント側バリデーション
        ValidationResult validation = validationService.validateTweetForm(content);
        if (!validation.isValid()) {
            throw new ValidationException(validation.getErrors());
        }

        // 2. JWTトークン取得
        String jwtToken = sessionStorage.getJwtToken()
            .orElseThrow(() -> new UnauthorizedException("ログインが必要です"));

        // 3. Backend APIでツイート投稿
        TweetDto tweetDto = apiRepository.createTweet(jwtToken, content);

        // 4. DTOをViewModelに変換
        return mapToViewModel(tweetDto);
    }
}
```

##### DisplayUserProfileUseCase
```java
package com.chirper.frontend.application.usecase;

@Service
public class DisplayUserProfileUseCase {
    private final IBackendApiRepository apiRepository;
    private final SessionStorageService sessionStorage;

    public UserProfileViewModel execute(String username) {
        // 1. Backend APIからユーザープロフィール取得
        UserProfileDto profileDto = apiRepository.getUserProfile(username);

        // 2. 現在のユーザーIDを取得（ログイン中の場合）
        Optional<String> currentUserId = sessionStorage.getCurrentUserId();

        // 3. DTOをViewModelに変換
        return mapToViewModel(profileDto, currentUserId);
    }
}
```

##### FollowUserUseCase
```java
package com.chirper.frontend.application.usecase;

@Service
public class FollowUserUseCase {
    private final IBackendApiRepository apiRepository;
    private final SessionStorageService sessionStorage;

    public void execute(String userId) {
        // 1. JWTトークン取得
        String jwtToken = sessionStorage.getJwtToken()
            .orElseThrow(() -> new UnauthorizedException("ログインが必要です"));

        // 2. Backend APIでフォロー実行
        apiRepository.followUser(jwtToken, userId);
    }
}
```

##### UpdateProfileUseCase
```java
package com.chirper.frontend.application.usecase;

@Service
public class UpdateProfileUseCase {
    private final IBackendApiRepository apiRepository;
    private final IClientValidationService validationService;
    private final SessionStorageService sessionStorage;

    public UserProfileViewModel execute(String displayName, String bio, String avatarUrl) {
        // 1. クライアント側バリデーション
        ValidationResult validation = validationService.validateProfileEditForm(
            displayName, bio, avatarUrl
        );
        if (!validation.isValid()) {
            throw new ValidationException(validation.getErrors());
        }

        // 2. JWTトークン取得
        String jwtToken = sessionStorage.getJwtToken()
            .orElseThrow(() -> new UnauthorizedException("ログインが必要です"));

        // 3. Backend APIでプロフィール更新
        UserProfileDto profileDto = apiRepository.updateProfile(
            jwtToken, displayName, bio, avatarUrl
        );

        // 4. DTOをViewModelに変換
        return mapToViewModel(profileDto, Optional.of(profileDto.getUserId()));
    }
}
```

#### 2.2 DTOs（Data Transfer Objects）

##### TimelineDto
```java
package com.chirper.frontend.application.dto;

public class TimelineDto {
    private List<TweetDto> tweets;
    private int currentPage;
    private int totalPages;
}
```

##### TweetDto
```java
package com.chirper.frontend.application.dto;

public class TweetDto {
    private String tweetId;
    private String userId;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String content;
    private String createdAt; // ISO 8601形式
    private int likesCount;
    private int retweetsCount;
    private boolean likedByCurrentUser;
    private boolean retweetedByCurrentUser;
}
```

##### UserProfileDto
```java
package com.chirper.frontend.application.dto;

public class UserProfileDto {
    private String userId;
    private String username;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private int followersCount;
    private int followingCount;
    private boolean followedByCurrentUser;
    private List<TweetDto> userTweets;
}
```

##### LoginResponse
```java
package com.chirper.frontend.application.dto;

public class LoginResponse {
    private String token;
    private String userId;
    private String username;
    private String expiresAt; // ISO 8601形式
}
```

##### RegisterResponse
```java
package com.chirper.frontend.application.dto;

public class RegisterResponse {
    private String userId;
    private String message;
}
```

### 3. Infrastructure Layer（インフラストラクチャ層）

**責務**: Backend APIとの統合、外部サービスとの連携

#### 3.1 Repository Implementation

##### BackendApiRepositoryImpl
```java
package com.chirper.frontend.infrastructure.api;

@Repository
public class BackendApiRepositoryImpl implements IBackendApiRepository {
    private final TimelineApiClient timelineApiClient;
    private final TweetApiClient tweetApiClient;
    private final UserApiClient userApiClient;

    @Override
    public LoginResponse login(String username, String password) {
        return timelineApiClient.login(username, password);
    }

    @Override
    public TimelineDto getTimeline(String jwtToken, int page, int size) {
        return timelineApiClient.getTimeline(jwtToken, page, size);
    }

    // ... その他のメソッド実装
}
```

#### 3.2 API Clients

**設計上の注意: WebClient `block()` の使用について**

このアプリケーションは**Spring MVC（同期モデル）**を採用しているため、WebClientの`block()`メソッドを使用して同期的にレスポンスを取得しています。これはリアクティブプログラミングの観点からはアンチパターンですが、以下の理由により正当化されます：

1. **アーキテクチャの整合性**: Spring MVCコントローラは同期的にレスポンスを返す必要があります。Thymeleafテンプレートエンジンも同期的なデータバインディングを前提としています。

2. **チームの技術スタック**: Backend Service側も同期モデル（Spring MVC）を採用しており、チーム全体で統一されたプログラミングモデルを維持することで、学習コストと認知負荷を低減します。

3. **シンプルさの優先**: この段階ではフルリアクティブスタック（Spring WebFlux + Reactor）の複雑さを導入せず、理解しやすい同期モデルで実装します。

4. **パフォーマンス要件**: 現時点のスループット要件では、スレッドプールベースの同期モデルで十分です。将来的にスケーラビリティが課題になった場合は、Spring WebFluxへの移行を検討します。

**代替案の検討**:
- `RestTemplate`の使用も検討しましたが、Spring 5以降は非推奨（deprecated）であり、WebClientが推奨されています。
- WebClientは非同期処理のサポートも提供するため、将来的なリアクティブ化への移行パスを残しています。

##### TimelineApiClient
```java
package com.chirper.frontend.infrastructure.client;

@Component
public class TimelineApiClient {
    private final WebClient webClient;

    @Value("${backend.api.base-url}")
    private String backendApiBaseUrl;

    public LoginResponse login(String username, String password) {
        return webClient.post()
            .uri(backendApiBaseUrl + "/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new LoginRequest(username, password))
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, this::handleClientError)
            .onStatus(HttpStatus::is5xxServerError, this::handleServerError)
            .bodyToMono(LoginResponse.class)
            .timeout(Duration.ofSeconds(5))
            .retry(3) // リトライ3回
            .block();
    }

    public TimelineDto getTimeline(String jwtToken, int page, int size) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(backendApiBaseUrl + "/api/v1/timeline")
                .queryParam("page", page)
                .queryParam("size", size)
                .build())
            .header("Authorization", "Bearer " + jwtToken)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, this::handleClientError)
            .onStatus(HttpStatus::is5xxServerError, this::handleServerError)
            .bodyToMono(TimelineDto.class)
            .timeout(Duration.ofSeconds(5))
            .block();
    }

    private Mono<? extends Throwable> handleClientError(ClientResponse response) {
        return response.bodyToMono(ErrorResponse.class)
            .flatMap(error -> {
                if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                    return Mono.error(new UnauthorizedException(error.getMessage()));
                } else if (response.statusCode() == HttpStatus.BAD_REQUEST) {
                    return Mono.error(new ValidationException(error.getDetails()));
                } else {
                    return Mono.error(new ApiClientException(error.getMessage()));
                }
            });
    }

    private Mono<? extends Throwable> handleServerError(ClientResponse response) {
        return Mono.error(new ApiServerException("Backend APIでエラーが発生しました"));
    }
}
```

##### TweetApiClient
```java
package com.chirper.frontend.infrastructure.client;

@Component
public class TweetApiClient {
    private final WebClient webClient;

    @Value("${backend.api.base-url}")
    private String backendApiBaseUrl;

    public TweetDto createTweet(String jwtToken, String content) {
        return webClient.post()
            .uri(backendApiBaseUrl + "/api/v1/tweets")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new CreateTweetRequest(content))
            .retrieve()
            .bodyToMono(TweetDto.class)
            .timeout(Duration.ofSeconds(5))
            .block();
    }

    public void deleteTweet(String jwtToken, String tweetId) {
        webClient.delete()
            .uri(backendApiBaseUrl + "/api/v1/tweets/" + tweetId)
            .header("Authorization", "Bearer " + jwtToken)
            .retrieve()
            .toBodilessEntity()
            .timeout(Duration.ofSeconds(5))
            .block();
    }

    public void likeTweet(String jwtToken, String tweetId) {
        webClient.post()
            .uri(backendApiBaseUrl + "/api/v1/tweets/" + tweetId + "/like")
            .header("Authorization", "Bearer " + jwtToken)
            .retrieve()
            .toBodilessEntity()
            .timeout(Duration.ofSeconds(5))
            .block();
    }

    public void unlikeTweet(String jwtToken, String tweetId) {
        webClient.delete()
            .uri(backendApiBaseUrl + "/api/v1/tweets/" + tweetId + "/like")
            .header("Authorization", "Bearer " + jwtToken)
            .retrieve()
            .toBodilessEntity()
            .timeout(Duration.ofSeconds(5))
            .block();
    }

    public void retweet(String jwtToken, String tweetId) {
        webClient.post()
            .uri(backendApiBaseUrl + "/api/v1/tweets/" + tweetId + "/retweet")
            .header("Authorization", "Bearer " + jwtToken)
            .retrieve()
            .toBodilessEntity()
            .timeout(Duration.ofSeconds(5))
            .block();
    }
}
```

##### UserApiClient
```java
package com.chirper.frontend.infrastructure.client;

@Component
public class UserApiClient {
    private final WebClient webClient;

    @Value("${backend.api.base-url}")
    private String backendApiBaseUrl;

    public UserProfileDto getUserProfile(String username) {
        return webClient.get()
            .uri(backendApiBaseUrl + "/api/v1/users/" + username)
            .retrieve()
            .bodyToMono(UserProfileDto.class)
            .timeout(Duration.ofSeconds(5))
            .block();
    }

    public UserProfileDto updateProfile(String jwtToken, String displayName, String bio, String avatarUrl) {
        return webClient.put()
            .uri(backendApiBaseUrl + "/api/v1/users/profile")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UpdateProfileRequest(displayName, bio, avatarUrl))
            .retrieve()
            .bodyToMono(UserProfileDto.class)
            .timeout(Duration.ofSeconds(5))
            .block();
    }

    public void followUser(String jwtToken, String userId) {
        webClient.post()
            .uri(backendApiBaseUrl + "/api/v1/users/" + userId + "/follow")
            .header("Authorization", "Bearer " + jwtToken)
            .retrieve()
            .toBodilessEntity()
            .timeout(Duration.ofSeconds(5))
            .block();
    }

    public void unfollowUser(String jwtToken, String userId) {
        webClient.delete()
            .uri(backendApiBaseUrl + "/api/v1/users/" + userId + "/follow")
            .header("Authorization", "Bearer " + jwtToken)
            .retrieve()
            .toBodilessEntity()
            .timeout(Duration.ofSeconds(5))
            .block();
    }
}
```

#### 3.3 Domain Service Implementation

##### TimelineFormattingServiceImpl
```java
package com.chirper.frontend.infrastructure.service;

@Service
public class TimelineFormattingServiceImpl implements ITimelineFormattingService {

    @Override
    public TimelineViewModel formatTimeline(List<TweetDto> tweets, int currentPage, int totalPages) {
        List<TweetViewModel> tweetViewModels = tweets.stream()
            .map(this::mapToTweetViewModel)
            .toList();

        return new TimelineViewModel(
            tweetViewModels,
            currentPage,
            totalPages,
            currentPage < totalPages - 1
        );
    }

    private TweetViewModel mapToTweetViewModel(TweetDto dto) {
        TweetContent content = new TweetContent(dto.getContent());
        DisplayTimestamp timestamp = new DisplayTimestamp(Instant.parse(dto.getCreatedAt()));

        return new TweetViewModel(
            dto.getTweetId(),
            dto.getUserId(),
            dto.getUsername(),
            dto.getDisplayName(),
            dto.getAvatarUrl(),
            content,
            timestamp,
            dto.getLikesCount(),
            dto.getRetweetsCount(),
            dto.isLikedByCurrentUser(),
            dto.isRetweetedByCurrentUser()
        );
    }
}
```

##### ContentRenderingServiceImpl
```java
package com.chirper.frontend.infrastructure.service;

@Service
public class ContentRenderingServiceImpl implements IContentRenderingService {

    @Override
    public String renderTweetContent(TweetContent content) {
        return content.toHighlightedHtml();
    }
}
```

##### ClientValidationServiceImpl
```java
package com.chirper.frontend.infrastructure.service;

@Service
public class ClientValidationServiceImpl implements IClientValidationService {

    @Override
    public ValidationResult validateLoginForm(String username, String password) {
        List<FieldError> errors = new ArrayList<>();

        if (username == null || username.isBlank()) {
            errors.add(new FieldError("username", "ユーザー名は必須です"));
        } else if (username.length() < 3 || username.length() > 20) {
            errors.add(new FieldError("username", "ユーザー名は3-20文字である必要があります"));
        }

        if (password == null || password.isBlank()) {
            errors.add(new FieldError("password", "パスワードは必須です"));
        } else if (password.length() < 8) {
            errors.add(new FieldError("password", "パスワードは8文字以上である必要があります"));
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }

    @Override
    public ValidationResult validateRegistrationForm(String username, String email, String password, String passwordConfirm) {
        List<FieldError> errors = new ArrayList<>();

        // ユーザー名バリデーション
        if (username == null || username.isBlank()) {
            errors.add(new FieldError("username", "ユーザー名は必須です"));
        } else if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
            errors.add(new FieldError("username", "ユーザー名は3-20文字の半角英数字・アンダースコアのみ使用可能です"));
        }

        // メールアドレスバリデーション
        if (email == null || email.isBlank()) {
            errors.add(new FieldError("email", "メールアドレスは必須です"));
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.add(new FieldError("email", "有効なメールアドレスを入力してください"));
        }

        // パスワードバリデーション
        if (password == null || password.isBlank()) {
            errors.add(new FieldError("password", "パスワードは必須です"));
        } else if (password.length() < 8) {
            errors.add(new FieldError("password", "パスワードは8文字以上である必要があります"));
        } else if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")) {
            errors.add(new FieldError("password", "パスワードは英数字記号を含む必要があります"));
        }

        // パスワード確認バリデーション
        if (passwordConfirm == null || !passwordConfirm.equals(password)) {
            errors.add(new FieldError("passwordConfirm", "パスワードが一致しません"));
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }

    @Override
    public ValidationResult validateTweetForm(String content) {
        List<FieldError> errors = new ArrayList<>();

        if (content == null || content.isBlank()) {
            errors.add(new FieldError("content", "ツイート本文は必須です"));
        } else if (content.length() > 280) {
            errors.add(new FieldError("content", "ツイートは280文字以内である必要があります"));
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }

    @Override
    public ValidationResult validateProfileEditForm(String displayName, String bio, String avatarUrl) {
        List<FieldError> errors = new ArrayList<>();

        if (displayName != null && displayName.length() > 50) {
            errors.add(new FieldError("displayName", "表示名は50文字以内である必要があります"));
        }

        if (bio != null && bio.length() > 160) {
            errors.add(new FieldError("bio", "自己紹介は160文字以内である必要があります"));
        }

        if (avatarUrl != null && !avatarUrl.isBlank() && !avatarUrl.matches("^https?://.+")) {
            errors.add(new FieldError("avatarUrl", "有効なURLを入力してください"));
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }
}
```

#### 3.4 Session Management

##### SessionStorageService
```java
package com.chirper.frontend.infrastructure.session;

@Service
public class SessionStorageService {
    private static final String JWT_TOKEN_KEY = "jwt_token";
    private static final String USER_ID_KEY = "user_id";
    private static final String USERNAME_KEY = "username";

    public void storeAuthInfo(String jwtToken, String userId, String username) {
        HttpSession session = getCurrentSession();
        session.setAttribute(JWT_TOKEN_KEY, jwtToken);
        session.setAttribute(USER_ID_KEY, userId);
        session.setAttribute(USERNAME_KEY, username);
    }

    public Optional<String> getJwtToken() {
        HttpSession session = getCurrentSession();
        return Optional.ofNullable((String) session.getAttribute(JWT_TOKEN_KEY));
    }

    public Optional<String> getCurrentUserId() {
        HttpSession session = getCurrentSession();
        return Optional.ofNullable((String) session.getAttribute(USER_ID_KEY));
    }

    public Optional<String> getCurrentUsername() {
        HttpSession session = getCurrentSession();
        return Optional.ofNullable((String) session.getAttribute(USERNAME_KEY));
    }

    public void clearAuthInfo() {
        HttpSession session = getCurrentSession();
        session.removeAttribute(JWT_TOKEN_KEY);
        session.removeAttribute(USER_ID_KEY);
        session.removeAttribute(USERNAME_KEY);
    }

    public boolean isAuthenticated() {
        return getJwtToken().isPresent();
    }

    private HttpSession getCurrentSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }
}
```

##### JwtTokenService
```java
package com.chirper.frontend.infrastructure.session;

@Service
public class JwtTokenService {

    // JWTトークンから有効期限を取得
    public Instant getExpirationTime(String jwtToken) {
        // JWT解析（jjwtライブラリ使用）
        Claims claims = Jwts.parser()
            .setSigningKey(getSigningKey())
            .parseClaimsJws(jwtToken)
            .getBody();

        return claims.getExpiration().toInstant();
    }

    // JWTトークンが有効か検証
    public boolean isTokenValid(String jwtToken) {
        try {
            Instant expiration = getExpirationTime(jwtToken);
            return Instant.now().isBefore(expiration);
        } catch (JwtException e) {
            return false;
        }
    }

    // JWTトークンからユーザーIDを取得
    public String getUserIdFromToken(String jwtToken) {
        Claims claims = Jwts.parser()
            .setSigningKey(getSigningKey())
            .parseClaimsJws(jwtToken)
            .getBody();

        return claims.getSubject();
    }

    private String getSigningKey() {
        // Backend Serviceと同じ署名鍵を使用
        // 本番環境では環境変数から取得
        return System.getenv("JWT_SIGNING_KEY");
    }
}
```

#### 3.5 Infrastructure Configuration

##### WebClientConfiguration
```java
package com.chirper.frontend.infrastructure.config;

@Configuration
public class WebClientConfiguration {

    @Value("${backend.api.base-url}")
    private String backendApiBaseUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(backendApiBaseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}
```

##### SecurityConfiguration
```java
package com.chirper.frontend.infrastructure.config;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'")
                )
                .frameOptions(frameOptions -> frameOptions.deny())
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentTypeOptions(contentType -> contentType.disable())
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        return http.build();
    }
}
```

### 4. Presentation Layer（プレゼンテーション層）

**責務**: HTTPリクエスト受付、Thymeleafレンダリング

#### 4.1 Controllers

##### HomeController
```java
package com.chirper.frontend.presentation.controller;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }
}
```

##### AuthController
```java
package com.chirper.frontend.presentation.controller;

@Controller
public class AuthController {
    private final IBackendApiRepository apiRepository;
    private final SessionStorageService sessionStorage;
    private final IClientValidationService validationService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form,
                       BindingResult bindingResult,
                       Model model) {
        // クライアント側バリデーション
        ValidationResult validation = validationService.validateLoginForm(
            form.getUsername(), form.getPassword()
        );

        if (!validation.isValid()) {
            validation.getErrors().forEach(error ->
                bindingResult.rejectValue(error.getField(), "error." + error.getField(), error.getMessage())
            );
            return "login";
        }

        try {
            // Backend APIでログイン
            LoginResponse response = apiRepository.login(form.getUsername(), form.getPassword());

            // セッションストレージに保存
            sessionStorage.storeAuthInfo(response.getToken(), response.getUserId(), response.getUsername());

            // タイムライン画面へリダイレクト
            return "redirect:/timeline";
        } catch (UnauthorizedException e) {
            model.addAttribute("error", "ユーザー名またはパスワードが正しくありません");
            return "login";
        } catch (ApiServerException e) {
            model.addAttribute("error", "サーバーエラーが発生しました。しばらく待ってから再度お試しください。");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        // クライアント側バリデーション
        ValidationResult validation = validationService.validateRegistrationForm(
            form.getUsername(), form.getEmail(), form.getPassword(), form.getPasswordConfirm()
        );

        if (!validation.isValid()) {
            validation.getErrors().forEach(error ->
                bindingResult.rejectValue(error.getField(), "error." + error.getField(), error.getMessage())
            );
            return "register";
        }

        try {
            // Backend APIで登録
            RegisterResponse response = apiRepository.register(
                form.getUsername(), form.getEmail(), form.getPassword()
            );

            // 成功メッセージをフラッシュスコープに保存
            redirectAttributes.addFlashAttribute("message", "登録が完了しました。ログインしてください。");

            // ログイン画面へリダイレクト
            return "redirect:/login";
        } catch (ValidationException e) {
            e.getErrors().forEach(error ->
                bindingResult.rejectValue(error.getField(), "error." + error.getField(), error.getMessage())
            );
            return "register";
        } catch (ApiServerException e) {
            bindingResult.reject("error.global", "サーバーエラーが発生しました。しばらく待ってから再度お試しください。");
            return "register";
        }
    }

    @PostMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes) {
        sessionStorage.clearAuthInfo();
        redirectAttributes.addFlashAttribute("message", "ログアウトしました");
        return "redirect:/login";
    }
}
```

##### TimelineController
```java
package com.chirper.frontend.presentation.controller;

@Controller
public class TimelineController {
    private final DisplayTimelineUseCase displayTimelineUseCase;

    @GetMapping("/timeline")
    public String timeline(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "20") int size,
                          Model model) {
        try {
            TimelineViewModel viewModel = displayTimelineUseCase.execute(page, size);
            model.addAttribute("timeline", viewModel);
            return "timeline";
        } catch (UnauthorizedException e) {
            return "redirect:/login";
        } catch (ApiServerException e) {
            model.addAttribute("error", "タイムラインの読み込みに失敗しました");
            return "error";
        }
    }
}
```

##### TweetController
```java
package com.chirper.frontend.presentation.controller;

@Controller
public class TweetController {
    private final SubmitTweetUseCase submitTweetUseCase;

    @PostMapping("/tweets")
    public String createTweet(@Valid @ModelAttribute("tweetForm") TweetForm form,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "ツイートの投稿に失敗しました");
            return "redirect:/timeline";
        }

        try {
            submitTweetUseCase.execute(form.getContent());
            redirectAttributes.addFlashAttribute("message", "ツイートを投稿しました");
            return "redirect:/timeline";
        } catch (UnauthorizedException e) {
            return "redirect:/login";
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("error", e.getErrors().get(0).getMessage());
            return "redirect:/timeline";
        }
    }
}
```

##### ProfileController
```java
package com.chirper.frontend.presentation.controller;

@Controller
public class ProfileController {
    private final DisplayUserProfileUseCase displayUserProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, Model model) {
        try {
            UserProfileViewModel viewModel = displayUserProfileUseCase.execute(username);
            model.addAttribute("profile", viewModel);
            return "profile";
        } catch (NotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        // 現在のユーザーのプロフィール取得
        String username = sessionStorage.getCurrentUsername()
            .orElseThrow(() -> new UnauthorizedException("ログインが必要です"));

        UserProfileViewModel viewModel = displayUserProfileUseCase.execute(username);
        model.addAttribute("profileForm", new ProfileForm(viewModel));
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileForm form,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "profile-edit";
        }

        try {
            UserProfileViewModel viewModel = updateProfileUseCase.execute(
                form.getDisplayName(), form.getBio(), form.getAvatarUrl()
            );

            redirectAttributes.addFlashAttribute("message", "プロフィールを更新しました");
            return "redirect:/profile/" + viewModel.getUsername();
        } catch (UnauthorizedException e) {
            return "redirect:/login";
        } catch (ValidationException e) {
            e.getErrors().forEach(error ->
                bindingResult.rejectValue(error.getField(), "error." + error.getField(), error.getMessage())
            );
            return "profile-edit";
        }
    }
}
```

#### 4.2 Exception Handlers

##### GlobalExceptionHandler
```java
package com.chirper.frontend.presentation.exception;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorizedException(UnauthorizedException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "セッションが期限切れです。再度ログインしてください。");
        return "redirect:/login";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(ApiServerException.class)
    public String handleApiServerException(ApiServerException e, Model model) {
        model.addAttribute("error", "サーバーエラーが発生しました。しばらく待ってから再度お試しください。");
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e, Model model) {
        model.addAttribute("error", "予期しないエラーが発生しました");
        return "error/500";
    }
}
```

#### 4.3 Thymeleaf Templates

##### timeline.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>タイムライン - Chirper</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" th:href="@{/css/style.css}">
</head>
<body>
    <!-- ナビゲーションバー -->
    <div th:replace="~{components/navbar :: navbar}"></div>

    <div class="container mt-4">
        <!-- ツイート投稿フォーム -->
        <div th:replace="~{components/tweet-form :: tweet-form}"></div>

        <!-- タイムライン -->
        <div class="timeline mt-4">
            <div th:if="${timeline.isEmpty()}" class="alert alert-info">
                まだツイートがありません。フォローしているユーザーのツイートがここに表示されます。
            </div>

            <div th:each="tweet : ${timeline.tweets}">
                <div th:replace="~{components/tweet :: tweet(tweet=${tweet})}"></div>
            </div>

            <!-- ページネーション -->
            <nav th:if="${timeline.hasNextPage()}" aria-label="Page navigation">
                <ul class="pagination justify-content-center mt-4">
                    <li class="page-item">
                        <a class="page-link" th:href="@{/timeline(page=${timeline.currentPage + 1})}">
                            もっと見る
                        </a>
                    </li>
                </ul>
            </nav>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
```

##### components/tweet.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
</head>
<body>
    <div th:fragment="tweet(tweet)" class="card mb-3">
        <div class="card-body">
            <div class="d-flex">
                <!-- アバター -->
                <img th:src="${tweet.avatarUrl}"
                     th:alt="${tweet.username}"
                     class="rounded-circle me-3"
                     width="48" height="48">

                <div class="flex-grow-1">
                    <!-- ユーザー情報 -->
                    <div class="d-flex justify-content-between">
                        <div>
                            <a th:href="@{/profile/{username}(username=${tweet.username})}" class="text-decoration-none">
                                <strong th:text="${tweet.displayName}">表示名</strong>
                                <span class="text-muted" th:text="'@' + ${tweet.username}">@username</span>
                            </a>
                            <span class="text-muted ms-2" th:text="${tweet.timestamp.toRelativeTime()}">3分前</span>
                        </div>

                        <!-- 削除ボタン（投稿者のみ表示） -->
                        <div th:if="${tweet.canDelete(#authentication.principal.userId)}">
                            <form th:action="@{/tweets/{id}/delete(id=${tweet.tweetId})}" method="post">
                                <button type="submit" class="btn btn-sm btn-outline-danger">削除</button>
                            </form>
                        </div>
                    </div>

                    <!-- ツイート本文（@mention、#hashtag、URLをハイライト） -->
                    <div class="mt-2" th:utext="${tweet.content.toHighlightedHtml()}">
                        ツイート本文
                    </div>

                    <!-- アクションボタン -->
                    <div class="d-flex mt-3">
                        <!-- いいねボタン -->
                        <form th:action="@{/tweets/{id}/like(id=${tweet.tweetId})}" method="post" class="me-4">
                            <button type="submit" class="btn btn-sm"
                                    th:classappend="${tweet.isLiked()} ? 'btn-danger' : 'btn-outline-secondary'">
                                <i class="bi bi-heart"></i>
                                <span th:text="${tweet.likesCount}">0</span>
                            </button>
                        </form>

                        <!-- リツイートボタン -->
                        <form th:action="@{/tweets/{id}/retweet(id=${tweet.tweetId})}" method="post">
                            <button type="submit" class="btn btn-sm"
                                    th:classappend="${tweet.isRetweeted()} ? 'btn-success' : 'btn-outline-secondary'">
                                <i class="bi bi-repeat"></i>
                                <span th:text="${tweet.retweetsCount}">0</span>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
```

##### components/tweet-form.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
</head>
<body>
    <div th:fragment="tweet-form" class="card">
        <div class="card-body">
            <form th:action="@{/tweets}" method="post">
                <div class="mb-3">
                    <textarea class="form-control"
                              id="tweetContent"
                              name="content"
                              rows="3"
                              maxlength="280"
                              placeholder="今何してる？"
                              th:field="*{tweetForm.content}"
                              required></textarea>
                    <div class="form-text" id="charCount">0 / 280</div>
                </div>
                <button type="submit" class="btn btn-primary" id="submitBtn">ツイート</button>
            </form>
        </div>
    </div>

    <script>
        // リアルタイム文字数カウント
        const textarea = document.getElementById('tweetContent');
        const charCount = document.getElementById('charCount');
        const submitBtn = document.getElementById('submitBtn');

        textarea.addEventListener('input', function() {
            const length = this.value.length;
            charCount.textContent = `${length} / 280`;

            if (length === 0 || length > 280) {
                submitBtn.disabled = true;
            } else {
                submitBtn.disabled = false;
            }
        });
    </script>
</body>
</html>
```

##### components/navbar.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
</head>
<body>
    <nav th:fragment="navbar" class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container-fluid">
            <a class="navbar-brand" th:href="@{/}">Chirper</a>

            <div class="collapse navbar-collapse">
                <ul class="navbar-nav ms-auto">
                    <!-- 認証済みユーザー -->
                    <li class="nav-item" th:if="${#authorization.expression('isAuthenticated()')}">
                        <a class="nav-link" th:href="@{/timeline}">タイムライン</a>
                    </li>
                    <li class="nav-item" th:if="${#authorization.expression('isAuthenticated()')}">
                        <a class="nav-link" th:href="@{/profile/{username}(username=${#authentication.principal.username})}">
                            プロフィール
                        </a>
                    </li>
                    <li class="nav-item" th:if="${#authorization.expression('isAuthenticated()')}">
                        <form th:action="@{/logout}" method="post" class="d-inline">
                            <button type="submit" class="btn btn-link nav-link">ログアウト</button>
                        </form>
                    </li>

                    <!-- 未認証ユーザー -->
                    <li class="nav-item" th:unless="${#authorization.expression('isAuthenticated()')}">
                        <a class="nav-link" th:href="@{/login}">ログイン</a>
                    </li>
                    <li class="nav-item" th:unless="${#authorization.expression('isAuthenticated()')}">
                        <a class="nav-link" th:href="@{/register}">登録</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</body>
</html>
```

## パッケージ構成

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── chirper/
│   │           └── frontend/
│   │               ├── domain/                          # Domain Layer（最内層）
│   │               │   ├── model/                       # ドメインモデル
│   │               │   │   ├── TimelineViewModel.java
│   │               │   │   ├── TweetViewModel.java
│   │               │   │   └── UserProfileViewModel.java
│   │               │   ├── valueobject/                 # 値オブジェクト
│   │               │   │   ├── TweetContent.java
│   │               │   │   ├── DisplayTimestamp.java
│   │               │   │   ├── HighlightedContent.java
│   │               │   │   ├── Mention.java
│   │               │   │   ├── Hashtag.java
│   │               │   │   ├── Url.java
│   │               │   │   ├── ValidationResult.java
│   │               │   │   └── FieldError.java
│   │               │   ├── service/                     # ドメインサービス（インターフェース）
│   │               │   │   ├── ITimelineFormattingService.java
│   │               │   │   ├── IContentRenderingService.java
│   │               │   │   └── IClientValidationService.java
│   │               │   └── repository/                  # リポジトリインターフェース
│   │               │       └── IBackendApiRepository.java
│   │               │
│   │               ├── application/                     # Application Layer
│   │               │   ├── usecase/                     # ユースケース
│   │               │   │   ├── DisplayTimelineUseCase.java
│   │               │   │   ├── SubmitTweetUseCase.java
│   │               │   │   ├── DisplayUserProfileUseCase.java
│   │               │   │   ├── FollowUserUseCase.java
│   │               │   │   └── UpdateProfileUseCase.java
│   │               │   └── dto/                         # Backend APIとのDTO
│   │               │       ├── TimelineDto.java
│   │               │       ├── TweetDto.java
│   │               │       ├── UserProfileDto.java
│   │               │       ├── LoginResponse.java
│   │               │       ├── RegisterResponse.java
│   │               │       ├── LoginRequest.java
│   │               │       ├── CreateTweetRequest.java
│   │               │       ├── UpdateProfileRequest.java
│   │               │       └── ErrorResponse.java
│   │               │
│   │               ├── infrastructure/                  # Infrastructure Layer
│   │               │   ├── api/                         # Backend API実装
│   │               │   │   └── BackendApiRepositoryImpl.java
│   │               │   ├── client/                      # APIクライアント
│   │               │   │   ├── TimelineApiClient.java
│   │               │   │   ├── TweetApiClient.java
│   │               │   │   └── UserApiClient.java
│   │               │   ├── service/                     # ドメインサービス実装
│   │               │   │   ├── TimelineFormattingServiceImpl.java
│   │               │   │   ├── ContentRenderingServiceImpl.java
│   │               │   │   └── ClientValidationServiceImpl.java
│   │               │   ├── config/                      # インフラ設定
│   │               │   │   ├── WebClientConfiguration.java
│   │               │   │   └── SecurityConfiguration.java
│   │               │   ├── session/                     # セッション管理
│   │               │   │   ├── SessionStorageService.java
│   │               │   │   └── JwtTokenService.java
│   │               │   └── exception/                   # 例外クラス
│   │               │       ├── ApiClientException.java
│   │               │       ├── ApiServerException.java
│   │               │       ├── UnauthorizedException.java
│   │               │       ├── ValidationException.java
│   │               │       └── NotFoundException.java
│   │               │
│   │               └── presentation/                    # Presentation Layer（最外層）
│   │                   ├── controller/                  # Spring MVCコントローラ
│   │                   │   ├── HomeController.java
│   │                   │   ├── AuthController.java
│   │                   │   ├── TimelineController.java
│   │                   │   ├── TweetController.java
│   │                   │   └── ProfileController.java
│   │                   ├── form/                        # フォームモデル
│   │                   │   ├── LoginForm.java
│   │                   │   ├── RegisterForm.java
│   │                   │   ├── TweetForm.java
│   │                   │   └── ProfileForm.java
│   │                   └── exception/                   # 例外ハンドラ
│   │                       ├── GlobalExceptionHandler.java
│   │                       └── ValidationExceptionHandler.java
│   │
│   └── resources/
│       ├── templates/                                   # Thymeleafテンプレート
│       │   ├── home.html
│       │   ├── login.html
│       │   ├── register.html
│       │   ├── timeline.html
│       │   ├── profile.html
│       │   ├── profile-edit.html
│       │   ├── components/
│       │   │   ├── navbar.html
│       │   │   ├── tweet.html
│       │   │   └── tweet-form.html
│       │   └── error/
│       │       ├── 404.html
│       │       └── 500.html
│       ├── static/
│       │   ├── css/
│       │   │   └── style.css
│       │   ├── js/
│       │   │   └── app.js
│       │   └── images/
│       │       └── default-avatar.png
│       └── application.yml
│
└── test/
    └── java/
        └── com/
            └── chirper/
                └── frontend/
                    ├── domain/                          # Domain層のテスト
                    │   ├── model/
                    │   ├── valueobject/
                    │   └── service/
                    ├── application/                     # Application層のテスト
                    │   └── usecase/
                    ├── infrastructure/                  # Infrastructure層のテスト
                    │   ├── client/
                    │   └── service/
                    └── presentation/                    # Presentation層のテスト（E2E）
                        └── controller/
```

## 技術スタック

### フレームワーク・ライブラリ

- **Java**: Java 21 LTS
- **Spring Boot**: 3.4.x
- **Spring Web**: 6.2.x（Spring MVC）
- **Thymeleaf**: 3.1.x（テンプレートエンジン）
- **Bootstrap**: 5.3.x（CSSフレームワーク、CDN経由）
- **Spring Security**: 6.3.x（認証・認可、CSRF保護）

### APIクライアント

- **WebClient**: Spring WebFlux（推奨）
- **Jackson**: JSON シリアライズ/デシリアライズ
- **jjwt**: JWT検証ライブラリ 0.13.0

### テスト

- **JUnit 5**: 単体テスト
- **Mockito**: モックフレームワーク
- **Selenide**: E2Eテスト（ブラウザ自動化）
- **WireMock**: Backend APIモック

### ビルドツール

- **Gradle**: 8.x（推奨）または Maven 3.9.x

## 設定ファイル

### application.yml

```yaml
spring:
  application:
    name: chirper-frontend

  thymeleaf:
    cache: false
    mode: HTML
    encoding: UTF-8

  security:
    user:
      name: user
      password: password

  session:
    timeout: 1h
    cookie:
      http-only: true
      secure: true
      same-site: strict

backend:
  api:
    base-url: ${BACKEND_API_BASE_URL:http://localhost:8080}

jwt:
  signing-key: ${JWT_SIGNING_KEY:your-secret-key}

logging:
  level:
    com.chirper.frontend: INFO
    org.springframework.web: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## データフロー

### タイムライン表示フロー

```
ユーザー
  ↓ GET /timeline
HomeController (Presentation)
  ↓ execute()
DisplayTimelineUseCase (Application)
  ↓ getTimeline(jwtToken, page, size)
BackendApiRepositoryImpl (Infrastructure)
  ↓ REST API呼び出し
Backend Service
  ↓ TimelineDto
BackendApiRepositoryImpl
  ↓ TimelineDto
DisplayTimelineUseCase
  ↓ formatTimeline()
TimelineFormattingServiceImpl (Infrastructure)
  ↓ TimelineViewModel
DisplayTimelineUseCase
  ↓ TimelineViewModel
HomeController
  ↓ Thymeleafレンダリング
timeline.html (Presentation)
  ↓ HTML
ユーザー
```

### ツイート投稿フロー

```
ユーザー
  ↓ POST /tweets (content)
TweetController (Presentation)
  ↓ execute(content)
SubmitTweetUseCase (Application)
  ↓ validateTweetForm(content)
ClientValidationServiceImpl (Infrastructure)
  ↓ ValidationResult
SubmitTweetUseCase
  ↓ createTweet(jwtToken, content)
BackendApiRepositoryImpl (Infrastructure)
  ↓ REST API呼び出し
Backend Service
  ↓ TweetDto
BackendApiRepositoryImpl
  ↓ TweetDto
SubmitTweetUseCase
  ↓ TweetViewModel
TweetController
  ↓ リダイレクト /timeline
ユーザー
```

## セキュリティ設計

### CSRF保護

- Spring SecurityのCSRF保護を有効化
- Thymeleafテンプレートに`th:csrf`トークンを自動挿入

### XSS対策

- Thymeleafの自動エスケープ（`th:text`使用）
- HTMLを表示する場合のみ`th:utext`を使用（@mention、#hashtag、URLのハイライト時）
- Content-Security-Policyヘッダー設定

### セッション管理

- セキュアなセッションクッキー（HttpOnly、Secure、SameSite属性）
- セッションタイムアウト: 1時間
- 最大セッション数: 1（同時ログイン制限）

### JWT検証

- Backend APIから受け取ったJWTトークンを検証
- 署名検証、有効期限チェック
- トークン無効時は自動ログアウト

## パフォーマンス最適化

### クライアント側最適化

1. **画像遅延ロード**: `loading="lazy"`属性でスクロール時にロード
2. **CSS/JSの最小化**: Bootstrap CDN使用、カスタムCSSは最小限
3. **キャッシュ制御**: 静的リソースのブラウザキャッシュ有効化

### API呼び出し最適化

1. **タイムアウト設定**: 5秒でタイムアウト
2. **リトライ**: 一時的なネットワークエラーは3回までリトライ
3. **ページネーション**: 1ページあたり20件で分割読み込み

## モニタリング・ロギング

### ログ出力項目

- リクエストID（トレーシング）
- API エンドポイント
- レスポンスタイム
- HTTPステータスコード
- エラースタックトレース

### ログレベル

- **INFO**: 正常系の操作（ログイン成功、ツイート投稿成功）
- **WARN**: 異常系の操作（バリデーションエラー、認証エラー）
- **ERROR**: システムエラー（Backend APIエラー、予期しない例外）

## 変更履歴

| 日付 | バージョン | 変更内容 | 担当者 |
|------|-----------|---------|--------|
| 2025-12-23T05:54:00.000Z | 1.0.0 | 初版作成（Frontend側の設計書、オニオンアーキテクチャ採用） | Claude Code |
