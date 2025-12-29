package com.chirper.e2e.tests;

import com.chirper.e2e.config.SelenideConfig;
import com.chirper.e2e.pages.LoginPage;
import com.chirper.e2e.pages.RegisterPage;
import com.chirper.e2e.pages.TimelinePage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.page;

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
        // 1. ユーザー登録
        RegisterPage registerPage = open("/register", RegisterPage.class);
        registerPage.registerAs("e2euser", "e2euser@example.com", "password123");

        // 2. 登録成功後、ログインページにリダイレクトされ、成功メッセージが表示される
        LoginPage loginPage = page(LoginPage.class);
        loginPage.shouldShowSuccess("登録が完了しました。ログインしてください");

        // 3. ログイン
        TimelinePage timelinePage = loginPage
            .loginAs("e2euser", "password123");

        // 4. タイムラインが表示される
        timelinePage.getHeader().shouldBe(visible);

        // 5. ログアウト
        timelinePage.logout();
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

        // ログインページにリダイレクトされる
        LoginPage loginPage = page(LoginPage.class);
        loginPage.getHeader().shouldBe(visible);
    }
}
