# chirper-frontend

Twitter風SNSアプリケーション「Chirper」のフロントエンドアプリケーション

## 技術スタック

- **Language**: Java 21
- **Framework**: Spring Boot 3.4.1
- **Template Engine**: Thymeleaf
- **Build Tool**: Gradle
- **E2E Testing**: Selenide 7.5.1

## 開発環境セットアップ

### 必要要件

- JDK 21以上
- Gradle 8.x以上

### ビルド

```bash
./gradlew build
```

### アプリケーション起動

```bash
./gradlew bootRun
```

アプリケーションは http://localhost:3000 で起動します。

## テスト

### ユニットテスト

```bash
./gradlew test
```

### E2Eテスト

#### ローカル環境での実行（ブラウザ表示）

```bash
./gradlew e2eTest
```

デフォルトではブラウザが表示され、テスト実行を目視確認できます。

#### ヘッドレスモードで実行（CI環境を再現）

```bash
HEADLESS=true ./gradlew e2eTest
```

環境変数 `HEADLESS=true` を設定すると、ブラウザを表示せずにテストを実行します。

#### 特定のテストクラスのみ実行

```bash
./gradlew e2eTest --tests "*AuthFlowTest"
./gradlew e2eTest --tests "*TweetFlowTest"
```

#### テストレポート確認

テスト実行後、以下のパスでHTMLレポートを確認できます：

```
build/reports/tests/e2eTest/index.html
```

### E2Eテストの構成

```
src/test/java/com/chirper/frontend/e2e/
├── config/
│   └── SelenideConfig.java       # Selenide設定（環境自動検出）
├── pages/ (5クラス)
│   ├── BasePage.java              # Page Object基底クラス
│   ├── LoginPage.java             # ログインページ
│   ├── RegisterPage.java          # ユーザー登録ページ
│   ├── TimelinePage.java          # タイムラインページ
│   └── ProfilePage.java           # プロフィールページ
└── tests/ (3クラス、12テストケース)
    ├── AuthFlowTest.java          # 認証フローテスト (4 tests)
    ├── TweetFlowTest.java         # ツイートフローテスト (4 tests)
    └── SocialFlowTest.java        # ソーシャルフローテスト (4 tests)
```

#### テストカバレッジ

| テストクラス | テスト数 | 検証内容 |
|------------|---------|---------|
| AuthFlowTest | 4 | ユーザー登録、ログイン、アクセス制御 |
| TweetFlowTest | 4 | ツイート投稿、いいね、削除 |
| SocialFlowTest | 4 | フォロー、アンフォロー、タイムライン |
| **合計** | **12** | **全クリティカルフロー** |

### E2Eテストのトラブルシューティング

#### Chrome/ChromeDriverのバージョン不一致

E2Eテストは WebDriverManager を使用して自動的にChromeDriverをダウンロードします。
エラーが発生した場合は、Chromeブラウザを最新バージョンに更新してください。

#### CI環境でのタイムアウト

CI環境では以下の設定が自動的に適用されます：
- `--headless=new`（新しいヘッドレスモード）
- `--no-sandbox`（サンドボックス無効化）
- `--disable-dev-shm-usage`（共有メモリ使用量削減）

タイムアウトが発生する場合は、`Configuration.timeout` の値を調整してください（デフォルト: 10秒）。

#### スクリーンショットとログの確認

テスト失敗時、スクリーンショットとページソースが以下のディレクトリに保存されます：

```
build/reports/tests/e2e/
```

### 詳細なE2Eテストガイド

より詳細なテスト実行手順、トラブルシューティング、ベストプラクティスについては、以下をご覧ください：

📘 **[E2E_TEST_GUIDE.md](./E2E_TEST_GUIDE.md)** - 包括的なE2Eテスト実行ガイド

## CI/CD

### GitHub Actions

Pull Request作成時やmainブランチへのpush時に、自動的にE2Eテストが実行されます。

- **Workflow**: `.github/workflows/e2e-tests.yml`
- **トリガー**: PR作成、mainブランチpush、手動実行、毎日0時（スケジュール）
- **タイムアウト**: 30分

テスト失敗時、スクリーンショットとテストレポートがArtifactとして保存されます。

## ライセンス

MIT License