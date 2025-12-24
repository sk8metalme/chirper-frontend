# Implementation Plan

## 1. プロジェクトセットアップ

- [x] 1.1 (P) プロジェクト初期構成
  - Spring Boot 3.4.x プロジェクトの作成（Gradle または Maven）
  - Java 21 LTS の設定
  - 基本的なディレクトリ構造の作成（domain, application, infrastructure, presentation パッケージ）
  - application.yml の基本設定（ポート、アプリケーション名）
  - _Requirements: 技術的制約_

- [x] 1.2 (P) 依存関係の設定
  - Spring Web、Thymeleaf、Spring Security の依存関係追加
  - WebFlux（WebClient 用）、Jackson、jjwt ライブラリの追加
  - Bootstrap 5.3 CDN の参照準備
  - JUnit 5、Mockito、Selenide、WireMock のテスト依存関係追加
  - _Requirements: 技術的制約_

## 2. Domain Layer 実装

- [x] 2.1 (P) ViewModel Entities の実装
  - TimelineViewModel の作成（ツイートリスト、ページング情報、次ページ判定ロジック）
  - TweetViewModel の作成（ツイート情報、削除可否判定、いいね/リツイート状態判定ロジック）
  - UserProfileViewModel の作成（ユーザー情報、フォローボタン表示判定、プロフィール編集可否判定ロジック）
  - _Requirements: 3.1, 3.2, 4.1, 5.1, 5.2_

- [x] 2.2 (P) Value Objects の実装
  - TweetContent の作成（280文字バリデーション、@mention/@hashtag/URL 抽出ロジック、HTML 変換ロジック）
  - DisplayTimestamp の作成（相対時刻表示ロジック、絶対時刻表示ロジック）
  - ValidationResult と FieldError の作成（バリデーション結果の表現）
  - Mention、Hashtag、Url クラスの作成
  - _Requirements: 4.1, 3.3, 7.1, 7.2, 7.3_

- [x] 2.3 (P) Domain Services インターフェースの定義
  - ITimelineFormattingService の定義（タイムライン整形ロジック）
  - IContentRenderingService の定義（ツイート本文 HTML 変換ロジック）
  - IClientValidationService の定義（各種フォームバリデーションロジック）
  - _Requirements: 1.1, 2.1, 4.1, 5.1_

- [x] 2.4 (P) Repository インターフェースの定義
  - IBackendApiRepository の定義（認証 API、タイムライン API、ツイート API、ユーザー API、ソーシャル API のメソッド定義）
  - _Requirements: 1.1, 2.1, 3.1, 4.1, 5.1, 6.1, 6.2, 6.3_

## 3. Infrastructure Layer 実装

- [x] 3.1 (P) WebClient 設定と例外クラス
  - WebClientConfiguration の実装（ベース URL、デフォルトヘッダー設定）
  - 例外クラスの作成（UnauthorizedException, ValidationException, ApiServerException, NotFoundException）
  - _Requirements: 技術的制約_

- [x] 3.2 Backend API クライアントの実装
  - TimelineApiClient の実装（ログイン、タイムライン取得、エラーハンドリング、タイムアウト・リトライ設定）
  - TweetApiClient の実装（ツイート投稿、削除、いいね、リツイート）
  - UserApiClient の実装（プロフィール取得、プロフィール更新、フォロー/アンフォロー）
  - _Requirements: 1.1, 2.1, 3.1, 4.1, 4.2, 5.1, 6.1, 6.2, 6.3_

- [x] 3.3 (P) Domain Services の実装
  - TimelineFormattingServiceImpl の実装（DTO から ViewModel への変換ロジック）
  - ContentRenderingServiceImpl の実装（ツイート本文のハイライト処理）
  - ClientValidationServiceImpl の実装（ログイン、登録、ツイート投稿、プロフィール編集のバリデーション）
  - _Requirements: 1.1, 2.1, 2.2, 4.1, 5.1_

- [x] 3.4 Session 管理の実装
  - SessionStorageService の実装（JWT トークン保存、取得、削除、認証状態判定）
  - JwtTokenService の実装（JWT トークン検証、有効期限チェック、ユーザー ID 取得）
  - _Requirements: 1.1, 1.3, セキュリティ要件_

- [x] 3.5 Security 設定の実装
  - SecurityConfiguration の実装（認証ルール、CSRF 保護、セキュリティヘッダー、セッション管理設定）
  - セキュアなセッションクッキー設定（HttpOnly、Secure、SameSite 属性）
  - _Requirements: セキュリティ要件_

- [x] 3.6 (P) Repository 実装クラスの作成
  - BackendApiRepositoryImpl の実装（各 API クライアントへの委譲）
  - _Requirements: 1.1, 2.1, 3.1, 4.1, 4.2, 5.1, 6.1, 6.2, 6.3_

## 4. Application Layer 実装

- [x] 4.1 DTOs の定義
  - TimelineDto、TweetDto、UserProfileDto の作成
  - LoginResponse、RegisterResponse の作成
  - LoginRequest、CreateTweetRequest、UpdateProfileRequest、ErrorResponse の作成
  - _Requirements: 1.1, 2.1, 3.1, 4.1, 5.1_

- [x] 4.2 認証関連 Use Cases の実装
  - LoginUseCase の実装（バリデーション、Backend API 呼び出し、セッション保存）
  - RegisterUseCase の実装（バリデーション、Backend API 呼び出し）
  - LogoutUseCase の実装（セッションクリア）
  - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2_

- [x] 4.3 (P) タイムライン関連 Use Cases の実装
  - DisplayTimelineUseCase の実装（JWT トークン取得、Backend API 呼び出し、ViewModel 変換）
  - _Requirements: 3.1, 3.2, 3.3_

- [x] 4.4 (P) ツイート関連 Use Cases の実装
  - SubmitTweetUseCase の実装（バリデーション、Backend API 呼び出し、ViewModel 変換）
  - DeleteTweetUseCase の実装（Backend API 呼び出し）
  - _Requirements: 4.1, 4.2_

- [x] 4.5 (P) プロフィール関連 Use Cases の実装
  - DisplayUserProfileUseCase の実装（Backend API 呼び出し、ViewModel 変換）
  - UpdateProfileUseCase の実装（バリデーション、Backend API 呼び出し、ViewModel 変換）
  - _Requirements: 5.1, 5.2_

- [x] 4.6 (P) ソーシャル機能 Use Cases の実装
  - FollowUserUseCase、UnfollowUserUseCase の実装（Backend API 呼び出し）
  - LikeTweetUseCase、UnlikeTweetUseCase の実装（Backend API 呼び出し）
  - RetweetUseCase の実装（Backend API 呼び出し）
  - _Requirements: 6.1, 6.2, 6.3_

## 5. Presentation Layer 実装

- [ ] 5.1 Form モデルの作成
  - LoginForm、RegisterForm、TweetForm、ProfileForm の作成
  - Bean Validation アノテーションの設定
  - _Requirements: 1.1, 2.1, 4.1, 5.1_

- [ ] 5.2 Controller の実装（認証）
  - HomeController の実装（ホーム画面表示）
  - AuthController の実装（ログイン画面表示、ログイン処理、登録画面表示、登録処理、ログアウト処理）
  - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2_

- [ ] 5.3 (P) Controller の実装（タイムライン）
  - TimelineController の実装（タイムライン画面表示、ページネーション）
  - _Requirements: 3.1, 3.2, 3.3_

- [ ] 5.4 (P) Controller の実装（ツイート）
  - TweetController の実装（ツイート投稿処理、削除処理）
  - _Requirements: 4.1, 4.2_

- [ ] 5.5 (P) Controller の実装（プロフィール）
  - ProfileController の実装（プロフィール画面表示、プロフィール編集画面表示、プロフィール更新処理）
  - _Requirements: 5.1, 5.2_

- [ ] 5.6 (P) Controller の実装（ソーシャル機能）
  - SocialController の実装（フォロー/アンフォロー処理、いいね/いいね取り消し処理、リツイート処理）
  - _Requirements: 6.1, 6.2, 6.3_

- [ ] 5.7 例外ハンドラーの実装
  - GlobalExceptionHandler の実装（UnauthorizedException、NotFoundException、ApiServerException、一般例外のハンドリング）
  - エラーページへのリダイレクト設定
  - _Requirements: エラーハンドリング_

- [ ] 5.8 Thymeleaf テンプレートの作成（基本画面）
  - home.html の作成（ホーム画面、サービス紹介、ログイン/登録ボタン）
  - login.html の作成（ログインフォーム、バリデーションエラー表示）
  - register.html の作成（登録フォーム、バリデーションエラー表示）
  - _Requirements: 1.1, 2.1_

- [ ] 5.9 Thymeleaf テンプレートの作成（タイムライン）
  - timeline.html の作成（タイムライン画面、ツイート投稿フォーム、タイムライン表示、ページネーション）
  - _Requirements: 3.1, 3.2, 3.3, 4.1_

- [ ] 5.10 (P) Thymeleaf テンプレートの作成（プロフィール）
  - profile.html の作成（プロフィール画面、ユーザー情報表示、ツイート一覧）
  - profile-edit.html の作成（プロフィール編集フォーム）
  - _Requirements: 5.1, 5.2_

- [ ] 5.11 (P) Thymeleaf コンポーネントの作成
  - components/navbar.html の作成（ナビゲーションバー、認証状態に応じた表示切り替え）
  - components/tweet.html の作成（ツイートカード、ユーザー情報、本文ハイライト、アクションボタン）
  - components/tweet-form.html の作成（ツイート投稿フォーム、リアルタイム文字数カウント、送信ボタン制御）
  - _Requirements: 3.1, 4.1, 7.1, 7.2, 7.3_

- [ ] 5.12 (P) Thymeleaf エラーページの作成
  - error/404.html の作成（ページが見つかりません）
  - error/500.html の作成（サーバーエラー）
  - _Requirements: エラーハンドリング_

- [ ] 5.13 (P) 静的リソースの作成
  - static/css/style.css の作成（カスタムスタイル、レスポンシブデザイン調整）
  - static/js/app.js の作成（クライアント側 JavaScript、文字数カウント、フォームバリデーション）
  - static/images/default-avatar.png の配置
  - _Requirements: ユーザビリティ要件_

## 6. テスト実装

- [ ] 6.1 Domain Layer の単体テスト
  - TweetContent のバリデーションと抽出ロジックのテスト
  - DisplayTimestamp の相対時刻表示ロジックのテスト
  - ViewModel のビジネスルールロジックのテスト
  - _Requirements: 非機能要件（テスト）_

- [ ] 6.2 Application Layer の単体テスト
  - 各 UseCase の正常系・異常系テスト（モック使用）
  - バリデーションエラーハンドリングのテスト
  - 認証エラーハンドリングのテスト
  - _Requirements: 非機能要件（テスト）_

- [ ] 6.3 Infrastructure Layer の単体テスト
  - API クライアントのテスト（WireMock 使用）
  - セッション管理のテスト
  - JWT トークン検証のテスト
  - バリデーションサービスのテスト
  - _Requirements: 非機能要件（テスト）_

- [ ] 6.4 Presentation Layer のテスト
  - Controller の単体テスト（MockMvc 使用）
  - フォームバリデーションのテスト
  - エラーハンドリングのテスト
  - _Requirements: 非機能要件（テスト）_

- [ ] 6.5 E2E テストの実装
  - ログイン・ログアウトフローのテスト（Selenide 使用）
  - ツイート投稿フローのテスト
  - タイムライン表示フローのテスト
  - プロフィール表示・編集フローのテスト
  - いいね・リツイートフローのテスト
  - _Requirements: 非機能要件（テスト）_

## 7. 統合とセットアップ

- [ ] 7.1 アプリケーション設定の完成
  - application.yml の完全な設定（Backend API URL、JWT 署名鍵、ログ設定、セキュリティ設定）
  - 環境変数の外部化（BACKEND_API_BASE_URL、JWT_SIGNING_KEY）
  - ログフォーマットと出力設定
  - _Requirements: 非機能要件（モニタリング・ロギング）_

- [ ] 7.2 ビルドとローカル実行の確認
  - Gradle/Maven ビルドの実行と成功確認
  - Spring Boot アプリケーションのローカル起動
  - Backend Service との接続確認
  - 各画面の動作確認（手動テスト）
  - _Requirements: すべての要件_

- [ ] 7.3* ベースラインテストカバレッジの達成
  - 単体テストカバレッジ 95% 以上の確認
  - E2E テストのすべてのクリティカルフローカバーの確認
  - テストレポートの生成と確認
  - _Requirements: 非機能要件（テスト）_

## Requirements Coverage

### 必須要件
- **1.1, 1.2, 1.3**: 認証・ログイン機能 → タスク 3.4, 4.2, 5.2
- **2.1, 2.2**: ユーザー登録機能 → タスク 4.2, 5.2
- **3.1, 3.2, 3.3**: タイムライン表示機能 → タスク 2.1, 4.3, 5.3, 5.9
- **4.1, 4.2**: ツイート投稿機能 → タスク 2.1, 2.2, 4.4, 5.4, 5.9, 5.11
- **5.1, 5.2**: プロフィール表示機能 → タスク 2.1, 4.5, 5.5, 5.10
- **6.1, 6.2, 6.3**: ソーシャル機能 → タスク 4.6, 5.6, 5.11
- **7.1, 7.2, 7.3**: コンテンツレンダリング機能 → タスク 2.2, 5.11

### 任意要件
- **8.1, 8.2, 8.3**: 画像添付、ハッシュタグ検索、ダークモード → 本フェーズでは実装せず

### 技術的制約・非機能要件
- **技術的制約**: タスク 1.1, 1.2, 2.1-2.4, 3.1-3.6
- **セキュリティ要件**: タスク 3.4, 3.5
- **エラーハンドリング**: タスク 3.1, 5.7, 5.12
- **ユーザビリティ要件**: タスク 5.13
- **非機能要件（テスト）**: タスク 6.1-6.5, 7.3
- **非機能要件（モニタリング・ロギング）**: タスク 7.1
