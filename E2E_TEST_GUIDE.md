# E2Eテスト実行ガイド

Chirper フロントエンドのE2Eテストを実行するための詳細ガイドです。

## 目次

1. [前提条件](#前提条件)
2. [テスト実行方法](#テスト実行方法)
3. [テストシナリオ](#テストシナリオ)
4. [トラブルシューティング](#トラブルシューティング)
5. [CI/CD環境での実行](#cicd環境での実行)

## 前提条件

### 必須要件

- **JDK 21以上** がインストールされていること
- **Google Chrome** 最新版がインストールされていること
- **バックエンドAPI** が起動していること（デフォルト: `http://localhost:8080`）
- **フロントエンドアプリケーション** が起動していること（デフォルト: `http://localhost:3000`）

### ChromeDriverについて

E2Eテストは **WebDriverManager** を使用して自動的にChromeDriverをダウンロードします。
手動でChromeDriverをインストールする必要はありません。

## テスト実行方法

### 1. ローカル環境での実行（ブラウザ表示）

テスト実行中にブラウザの動作を目視確認できます。

```bash
./gradlew e2eTest
```

**特徴:**
- Chromeブラウザが表示される
- テストの実行過程を確認できる
- デバッグに最適

### 2. ヘッドレスモードでの実行（CI環境を再現）

ブラウザを表示せずにバックグラウンドで実行します。

```bash
HEADLESS=true ./gradlew e2eTest
```

**特徴:**
- ブラウザウィンドウは表示されない
- CI環境と同じ動作を確認できる
- 高速に実行できる

### 3. 特定のテストクラスのみ実行

```bash
# 認証フローテストのみ
./gradlew e2eTest --tests "*AuthFlowTest"

# ツイートフローテストのみ
./gradlew e2eTest --tests "*TweetFlowTest"

# ソーシャルフローテストのみ
./gradlew e2eTest --tests "*SocialFlowTest"
```

### 4. 特定のテストメソッドのみ実行

```bash
./gradlew e2eTest --tests "*AuthFlowTest.testUserRegistrationAndLogin"
```

### 5. 並列実行（高速化）

```bash
./gradlew e2eTest --parallel --max-workers=4
```

**注意:** 並列実行時はテストデータの競合に注意してください。

## テストシナリオ

### 📝 AuthFlowTest（認証フローテスト）

| テストケース | 検証内容 |
|-------------|---------|
| `testUserRegistrationAndLogin` | ユーザー登録→ログイン→タイムライン表示 |
| `testLoginWithInvalidCredentials` | ログイン失敗時のエラーメッセージ表示 |
| `testUnauthorizedAccess` | 未ログイン時の保護されたページへのアクセス制御 |
| `testAccessAfterLogout` | ログアウト後のアクセス制御 |

### 🐦 TweetFlowTest（ツイートフローテスト）

| テストケース | 検証内容 |
|-------------|---------|
| `testPostTweetAndLike` | ツイート投稿→タイムライン表示→いいね |
| `testDeleteTweet` | ツイート削除 |
| `testMultipleTweets` | 複数ツイートの投稿 |
| `testEmptyTweetCannotBePosted` | 空のツイート検証 |

### 👥 SocialFlowTest（ソーシャルフローテスト）

| テストケース | 検証内容 |
|-------------|---------|
| `testFollowAndUnfollow` | フォロー→アンフォロー機能 |
| `testTimelineShowsFollowedUsersTweets` | フォローしたユーザーのツイートがタイムラインに表示 |
| `testFollowerFollowingCount` | フォロワー/フォロー数の更新 |
| `testCannotFollowAlreadyFollowedUser` | 既にフォロー中のユーザーを再度フォローしない |

## テスト結果の確認

### HTMLレポート

テスト実行後、以下のパスでHTMLレポートを確認できます：

```
build/reports/tests/e2eTest/index.html
```

ブラウザで開く：

```bash
open build/reports/tests/e2eTest/index.html  # macOS
```

### スクリーンショット（テスト失敗時）

テスト失敗時、スクリーンショットとページソースが以下のディレクトリに保存されます：

```
build/reports/tests/e2e/
```

### コンソール出力

テスト実行中のログは標準出力に表示されます。詳細なログを確認する場合：

```bash
./gradlew e2eTest --info
```

デバッグレベルのログ：

```bash
./gradlew e2eTest --debug
```

## トラブルシューティング

### ❌ Chrome/ChromeDriverのバージョン不一致

**症状:**
```
SessionNotCreatedException: session not created: This version of ChromeDriver only supports Chrome version XX
```

**解決方法:**
1. Google Chromeを最新版に更新
2. WebDriverManagerが自動的に適切なChromeDriverをダウンロードするので、Gradleキャッシュをクリア：

```bash
./gradlew clean
```

### ❌ タイムアウトエラー

**症状:**
```
TimeoutException: Wait condition failed
```

**解決方法:**

1. **バックエンドとフロントエンドが起動しているか確認**

```bash
# バックエンド確認
curl http://localhost:8080/actuator/health

# フロントエンド確認
curl http://localhost:3000
```

2. **タイムアウト値を調整** (`SelenideConfig.java`):

```java
Configuration.timeout = 15000;  // 10秒→15秒に延長
```

### ❌ ポート競合エラー

**症状:**
```
Address already in use: bind
```

**解決方法:**

使用中のポートを確認して、既存のプロセスを終了：

```bash
# ポート使用状況確認
lsof -i :3000
lsof -i :8080

# プロセス終了
kill -9 <PID>
```

### ❌ セレクタが見つからないエラー

**症状:**
```
ElementNotFound: Element not found {#login-button}
```

**解決方法:**

1. **フロントエンドのHTML構造を確認**
2. **Page Objectのセレクタを更新**
3. **待機条件を追加**:

```java
$("#login-button").shouldBe(visible, enabled).click();
```

### ❌ CI環境でのメモリ不足エラー

**症状:**
```
java.lang.OutOfMemoryError: Java heap space
```

**解決方法:**

`build.gradle`にヒープサイズ設定を追加：

```gradle
tasks.named('e2eTest') {
    maxHeapSize = '2g'
}
```

## CI/CD環境での実行

### GitHub Actions

Pull Request作成時やmainブランチへのpush時に自動的にE2Eテストが実行されます。

**Workflow**: `.github/workflows/e2e-tests.yml`

**トリガー:**
- Pull Request作成（main, developブランチ）
- mainブランチへのpush
- 手動実行（workflow_dispatch）
- 毎日0時（スケジュール実行）

### CI環境の特徴

CI環境では以下の設定が自動的に適用されます：

- `--headless=new`（新しいヘッドレスモード）
- `--no-sandbox`（サンドボックス無効化）
- `--disable-dev-shm-usage`（共有メモリ使用量削減）
- `--disable-gpu`（GPU無効化）

### Artifact保存

テスト失敗時、以下がArtifactとして保存されます（保持期間: 7日）：

- テストレポート（HTML）
- スクリーンショット
- ページソース

## 環境変数

### CI環境判定

以下の環境変数が設定されている場合、CI環境として判定されます：

- `CI=true`
- `GITHUB_ACTIONS=true`
- `JENKINS_HOME=/var/jenkins_home`

### カスタム設定

| 環境変数 | 説明 | デフォルト値 |
|---------|------|------------|
| `HEADLESS` | ヘッドレスモード強制 | `false` |
| `base.url` | フロントエンドURL | `http://localhost:3000` |

**使用例:**

```bash
HEADLESS=true base.url=http://localhost:8080 ./gradlew e2eTest
```

## ベストプラクティス

### ✅ DO（推奨）

1. **テスト実行前にバックエンド/フロントエンドを起動**
2. **ユニークなテストデータを使用**（`System.currentTimeMillis()`）
3. **テスト後のクリーンアップを実装**
4. **Page Objectパターンに従う**
5. **Fluent APIでメソッドチェーンを活用**

### ❌ DON'T（非推奨）

1. **XPathセレクタを使用しない**（IDまたはCSS classを使用）
2. **Thread.sleep()を使用しない**（Selenideの待機メソッドを使用）
3. **ハードコードされたテストデータを使用しない**
4. **ビジネスロジックをPage Objectに含めない**

## さらに詳しく

- **設計書**: `docs/michi/chirper/improvement-plans/e2e-testing-design.md`
- **タスク**: `docs/michi/chirper/improvement-plans/e2e-testing-tasks.md`
- **Selenide公式ドキュメント**: https://selenide.org/documentation.html
- **WebDriverManager**: https://bonigarcia.dev/webdrivermanager/

## サポート

問題が解決しない場合は、以下を確認してください：

1. Google Chromeを最新版に更新
2. JDK 21がインストールされているか確認
3. バックエンド/フロントエンドが正常に起動しているか確認
4. `build/reports/tests/e2eTest/index.html` でエラーの詳細を確認
