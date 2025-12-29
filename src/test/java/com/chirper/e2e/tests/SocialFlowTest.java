package com.chirper.e2e.tests;

import com.chirper.e2e.config.SelenideConfig;
import com.chirper.e2e.config.TestDataFixture;
import com.chirper.e2e.pages.LoginPage;
import com.chirper.e2e.pages.ProfilePage;
import com.chirper.e2e.pages.SearchPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@Tag("e2e")
@DisplayName("ソーシャルフローE2Eテスト")
@Disabled("検索ページ(/search)およびフォロー機能が未実装のため一時無効化")
class SocialFlowTest {

    @BeforeAll
    static void setup() {
        SelenideConfig.setup();
    }

    @BeforeEach
    void login() {
        LoginPage loginPage = open("/login", LoginPage.class);
        loginPage.loginAs(
            TestDataFixture.Users.TEST_USER_1_USERNAME,
            TestDataFixture.Users.TEST_USER_1_PASSWORD
        );
    }

    @Test
    @DisplayName("ユーザー検索→フォロー→フォロワー一覧確認")
    void testFollowUser() {
        // 1. ユーザー検索
        SearchPage searchPage = open("/search", SearchPage.class);
        searchPage.searchUser(TestDataFixture.Users.TEST_USER_2_USERNAME);

        // 2. フォローボタンをクリック
        searchPage.followUser(TestDataFixture.Users.TEST_USER_2_USERNAME);

        // 3. testuser2のプロフィールページへ移動
        ProfilePage profilePage = searchPage.openUserProfile(TestDataFixture.Users.TEST_USER_2_USERNAME);

        // 4. フォロワー一覧に表示されることを確認
        profilePage.openFollowersTab()
                   .shouldHaveFollower(TestDataFixture.Users.TEST_USER_1_USERNAME);
    }

    @Test
    @DisplayName("アンフォロー")
    void testUnfollowUser() {
        ProfilePage profilePage = open("/profile/" + TestDataFixture.Users.TEST_USER_2_USERNAME, ProfilePage.class);

        // 1. フォロー
        profilePage.clickFollowButton();
        profilePage.shouldShowFollowStatus("フォロー中");

        // 2. アンフォロー
        profilePage.clickUnfollowButton();
        profilePage.shouldShowFollowStatus("フォロー");
    }
}
