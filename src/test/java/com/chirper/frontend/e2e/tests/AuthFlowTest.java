package com.chirper.frontend.e2e.tests;

import com.chirper.frontend.e2e.config.SelenideConfig;
import com.chirper.frontend.e2e.pages.LoginPage;
import com.chirper.frontend.e2e.pages.RegisterPage;
import com.chirper.frontend.e2e.pages.TimelinePage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

/**
 * 認証フローE2Eテスト
 *
 * <p>ユーザー登録、ログイン、ログアウトの主要な認証フローを検証します。</p>
 *
 * <h3>テストシナリオ:</h3>
 * <ul>
 *   <li>ユーザー登録→ログイン→タイムライン表示の完全フロー</li>
 *   <li>ログイン失敗時のエラーメッセージ表示</li>
 *   <li>未ログイン時の保護されたページへのアクセス制御</li>
 * </ul>
 */
@Tag("e2e")
@DisplayName("認証フローE2Eテスト")
class AuthFlowTest {

    @BeforeAll
    static void setup() {
        SelenideConfig.setup();
    }

    @Test
    @DisplayName("ユーザー登録→ログイン→タイムライン表示")
    void testUserRegistrationAndLogin() {
        // ユニークなユーザー名を生成（同じテストを複数回実行可能にするため）
        String uniqueUsername = "e2euser_" + System.currentTimeMillis();
        String email = uniqueUsername + "@example.com";
        String password = "password123";

        // 1. ユーザー登録
        RegisterPage registerPage = open("/register", RegisterPage.class);
        LoginPage loginPage = registerPage.registerAs(uniqueUsername, email, password);

        // 2. ログイン
        TimelinePage timelinePage = loginPage.loginAs(uniqueUsername, password);

        // 3. タイムラインが表示される
        timelinePage.shouldBeVisible();

        // 4. ログアウト
        loginPage = timelinePage.logout();
        loginPage.shouldBeVisible();
    }

    @Test
    @DisplayName("ログイン失敗時のエラーメッセージ表示")
    void testLoginWithInvalidCredentials() {
        LoginPage loginPage = open("/login", LoginPage.class);

        loginPage
            .enterUsername("invaliduser")
            .enterPassword("wrongpassword")
            .clickLoginExpectingError()
            .shouldShowError("認証に失敗しました");
    }

    @Test
    @DisplayName("未ログイン時の保護されたページへのアクセス")
    void testUnauthorizedAccess() {
        // タイムラインに直接アクセス
        open("/timeline");

        // ログインページにリダイレクトされることを確認
        LoginPage loginPage = new LoginPage();
        loginPage.shouldBeVisible();
    }

    @Test
    @DisplayName("ログアウト後の保護されたページへのアクセス")
    void testAccessAfterLogout() {
        // ユニークなユーザー名を生成
        String uniqueUsername = "e2euser_" + System.currentTimeMillis();
        String email = uniqueUsername + "@example.com";
        String password = "password123";

        // 1. ユーザー登録とログイン
        RegisterPage registerPage = open("/register", RegisterPage.class);
        LoginPage loginPage = registerPage.registerAs(uniqueUsername, email, password);
        TimelinePage timelinePage = loginPage.loginAs(uniqueUsername, password);

        // 2. ログアウト
        loginPage = timelinePage.logout();

        // 3. タイムラインに再度アクセス試行
        open("/timeline");

        // 4. ログインページにリダイレクトされることを確認
        loginPage.shouldBeVisible();
    }
}
