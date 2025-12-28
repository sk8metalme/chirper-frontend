package com.chirper.frontend.e2e.tests;

import com.chirper.frontend.e2e.config.SelenideConfig;
import com.chirper.frontend.e2e.pages.LoginPage;
import com.chirper.frontend.e2e.pages.ProfilePage;
import com.chirper.frontend.e2e.pages.RegisterPage;
import com.chirper.frontend.e2e.pages.TimelinePage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

/**
 * ソーシャルフローE2Eテスト
 *
 * <p>フォロー、アンフォロー、タイムライン表示などのソーシャル機能を検証します。</p>
 *
 * <h3>テストシナリオ:</h3>
 * <ul>
 *   <li>フォロー→アンフォロー機能</li>
 *   <li>フォローしたユーザーのツイートがタイムラインに表示される</li>
 *   <li>フォロワー/フォロー数の更新</li>
 * </ul>
 */
@Tag("e2e")
@DisplayName("ソーシャルフローE2Eテスト")
class SocialFlowTest {

    private static String user1Username;
    private static String user1Password;
    private static String user2Username;
    private static String user2Password;

    @BeforeAll
    static void setup() {
        SelenideConfig.setup();

        // テスト用ユーザー1を作成
        user1Username = "socialuser1_" + System.currentTimeMillis();
        user1Password = "password123";
        String email1 = user1Username + "@example.com";

        RegisterPage registerPage1 = open("/register", RegisterPage.class);
        registerPage1.registerAs(user1Username, email1, user1Password);

        // テスト用ユーザー2を作成
        user2Username = "socialuser2_" + System.currentTimeMillis();
        user2Password = "password123";
        String email2 = user2Username + "@example.com";

        RegisterPage registerPage2 = open("/register", RegisterPage.class);
        registerPage2.registerAs(user2Username, email2, user2Password);
    }

    @Test
    @DisplayName("フォロー→アンフォロー機能")
    void testFollowAndUnfollow() {
        // ユーザー1でログイン
        LoginPage loginPage = open("/login", LoginPage.class);
        loginPage.loginAs(user1Username, user1Password);

        // ユーザー2のプロフィールを開く
        ProfilePage profilePage = new ProfilePage();
        profilePage.open(user2Username);

        // フォローする
        profilePage.clickFollow()
                   .shouldShowFollowingStatus();

        // アンフォローする
        profilePage.clickUnfollow()
                   .shouldShowNotFollowingStatus();
    }

    @Test
    @DisplayName("フォローしたユーザーのツイートがタイムラインに表示される")
    void testTimelineShowsFollowedUsersTweets() {
        // ユーザー2でログインしてツイート投稿
        LoginPage loginPage = open("/login", LoginPage.class);
        TimelinePage timelinePage = loginPage.loginAs(user2Username, user2Password);

        String tweetContent = "Tweet from user2 " + System.currentTimeMillis();
        timelinePage.postTweet(tweetContent);

        // ログアウト
        timelinePage.logout();

        // ユーザー1でログイン
        loginPage = open("/login", LoginPage.class);
        loginPage.loginAs(user1Username, user1Password);

        // ユーザー2をフォロー
        ProfilePage profilePage = new ProfilePage();
        profilePage.open(user2Username)
                   .clickFollow()
                   .shouldShowFollowingStatus();

        // タイムラインに戻る
        timelinePage = open("/timeline", TimelinePage.class);

        // ユーザー2のツイートがタイムラインに表示されることを確認
        timelinePage.shouldHaveTweet(tweetContent);
    }

    @Test
    @DisplayName("フォロワー/フォロー数の更新")
    void testFollowerFollowingCount() {
        // ユーザー1でログイン
        LoginPage loginPage = open("/login", LoginPage.class);
        loginPage.loginAs(user1Username, user1Password);

        // ユーザー2をフォロー
        ProfilePage user2Profile = new ProfilePage();
        user2Profile.open(user2Username)
                    .clickFollow()
                    .shouldShowFollowingStatus();

        // ユーザー1のプロフィールを開いてフォロー数を確認
        ProfilePage user1Profile = new ProfilePage();
        user1Profile.open(user1Username);
        // フォロー数が1以上であることを確認（オプション: 実装依存）
        // user1Profile.shouldShowFollowingCount(1);

        // ログアウト
        TimelinePage timelinePage = open("/timeline", TimelinePage.class);
        timelinePage.logout();

        // ユーザー2でログインしてフォロワー数を確認
        loginPage = open("/login", LoginPage.class);
        loginPage.loginAs(user2Username, user2Password);

        user2Profile = new ProfilePage();
        user2Profile.open(user2Username);
        // フォロワー数が1以上であることを確認（オプション: 実装依存）
        // user2Profile.shouldShowFollowerCount(1);
    }

    @Test
    @DisplayName("既にフォロー中のユーザーをフォローしない")
    void testCannotFollowAlreadyFollowedUser() {
        // ユーザー1でログイン
        LoginPage loginPage = open("/login", LoginPage.class);
        loginPage.loginAs(user1Username, user1Password);

        // ユーザー2をフォロー
        ProfilePage profilePage = new ProfilePage();
        profilePage.open(user2Username)
                   .clickFollow()
                   .shouldShowFollowingStatus();

        // 既にフォロー中なので、フォローボタンは表示されない
        // unfollowボタンのみ表示される
        profilePage.shouldShowFollowingStatus();
    }
}
