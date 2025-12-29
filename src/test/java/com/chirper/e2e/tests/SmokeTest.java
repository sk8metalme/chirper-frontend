package com.chirper.e2e.tests;

import com.chirper.e2e.config.SelenideConfig;
import com.chirper.e2e.pages.LoginPage;
import com.chirper.e2e.pages.RegisterPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@Tag("e2e")
@DisplayName("スモークテスト（基本的なページ表示確認）")
class SmokeTest {

    @BeforeAll
    static void setup() {
        SelenideConfig.setup();
    }

    @Test
    @DisplayName("ログインページが正常に表示される")
    void testLoginPageDisplays() {
        LoginPage loginPage = open("/login", LoginPage.class);

        // ページが表示されることを確認
        loginPage.getHeader().shouldBe(visible);
    }

    @Test
    @DisplayName("登録ページが正常に表示される")
    void testRegisterPageDisplays() {
        RegisterPage registerPage = open("/register", RegisterPage.class);

        // ページが表示されることを確認
        registerPage.getHeader().shouldBe(visible);
    }

    @Test
    @DisplayName("ホームページが正常に表示される")
    void testHomePageDisplays() {
        // ホームページにアクセス
        open("/");

        // ナビゲーションバーが表示されることを確認
        $("nav.navbar").shouldBe(visible);
    }
}
