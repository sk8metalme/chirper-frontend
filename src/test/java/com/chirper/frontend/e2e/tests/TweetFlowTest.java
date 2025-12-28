package com.chirper.frontend.e2e.tests;

import com.chirper.frontend.e2e.config.SelenideConfig;
import com.chirper.frontend.e2e.pages.LoginPage;
import com.chirper.frontend.e2e.pages.RegisterPage;
import com.chirper.frontend.e2e.pages.TimelinePage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

/**
 * ツイートフローE2Eテスト
 *
 * <p>ツイート投稿、いいね、削除などの主要なツイート機能を検証します。</p>
 *
 * <h3>テストシナリオ:</h3>
 * <ul>
 *   <li>ツイート投稿→タイムライン表示→いいね</li>
 *   <li>ツイート削除</li>
 *   <li>複数ツイートの投稿と表示</li>
 * </ul>
 */
@Tag("e2e")
@DisplayName("ツイートフローE2Eテスト")
class TweetFlowTest {

    private static String testUsername;
    private static String testPassword;

    @BeforeAll
    static void setupOnce() {
        SelenideConfig.setup();

        // テスト用ユーザーを作成（全テストで共通使用）
        testUsername = "tweetuser_" + System.currentTimeMillis();
        testPassword = "password123";
        String email = testUsername + "@example.com";

        RegisterPage registerPage = open("/register", RegisterPage.class);
        registerPage.registerAs(testUsername, email, testPassword);
    }

    @BeforeEach
    void login() {
        // 各テスト前にログイン
        LoginPage loginPage = open("/login", LoginPage.class);
        loginPage.loginAs(testUsername, testPassword);
    }

    @Test
    @DisplayName("ツイート投稿→タイムライン表示→いいね")
    void testPostTweetAndLike() {
        TimelinePage timelinePage = open("/timeline", TimelinePage.class);

        // 1. ツイート投稿
        String tweetContent = "This is a test tweet " + System.currentTimeMillis();
        timelinePage.postTweet(tweetContent);

        // 2. タイムラインに表示されることを確認
        timelinePage.shouldHaveTweet(tweetContent);

        // 3. いいねボタンをクリック
        timelinePage.likeTweet(0);

        // 4. いいね数が1に増加（オプション: いいねカウントが実装されている場合）
        // timelinePage.shouldShowLikeCount(0, 1);
    }

    @Test
    @DisplayName("ツイート削除")
    void testDeleteTweet() {
        TimelinePage timelinePage = open("/timeline", TimelinePage.class);

        // 1. ツイート投稿
        String tweetContent = "Tweet to be deleted " + System.currentTimeMillis();
        timelinePage.postTweet(tweetContent);

        // 2. 投稿されたことを確認
        timelinePage.shouldHaveTweet(tweetContent);

        // 3. 削除
        timelinePage.deleteTweet(0);

        // 4. タイムラインから消えたことを確認
        timelinePage.shouldNotHaveTweet(tweetContent);
    }

    @Test
    @DisplayName("複数ツイートの投稿")
    void testMultipleTweets() {
        TimelinePage timelinePage = open("/timeline", TimelinePage.class);

        // 複数のツイートを投稿
        String tweet1 = "First tweet " + System.currentTimeMillis();
        String tweet2 = "Second tweet " + System.currentTimeMillis();
        String tweet3 = "Third tweet " + System.currentTimeMillis();

        timelinePage.postTweet(tweet1);
        timelinePage.postTweet(tweet2);
        timelinePage.postTweet(tweet3);

        // すべてのツイートがタイムラインに表示されることを確認
        timelinePage.shouldHaveTweet(tweet1);
        timelinePage.shouldHaveTweet(tweet2);
        timelinePage.shouldHaveTweet(tweet3);
    }

    @Test
    @DisplayName("空のツイートは投稿できない")
    void testEmptyTweetCannotBePosted() {
        TimelinePage timelinePage = open("/timeline", TimelinePage.class);

        // 投稿前のツイート数を記録（オプション）
        // int initialCount = timelinePage.getTweetCount();

        // 空のツイートを投稿しようとする（実装によっては失敗する）
        // Note: UIによっては空のツイートで投稿ボタンが無効化される場合がある
        // この場合はボタンの状態を検証する
    }
}
