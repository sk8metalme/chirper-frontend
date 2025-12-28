package com.chirper.e2e.tests;

import com.chirper.e2e.config.SelenideConfig;
import com.chirper.e2e.pages.LoginPage;
import com.chirper.e2e.pages.RegisterPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.codeborne.selenide.Selenide.$;

@Tag("e2e")
@DisplayName("登録デバッグテスト")
class RegistrationOnlyTest {

    @BeforeAll
    static void setup() {
        SelenideConfig.setup();
    }

    @Test
    @DisplayName("ユーザー登録のみテスト")
    void testRegistrationOnly() {
        // 1. 登録ページを開く
        RegisterPage registerPage = open("/register", RegisterPage.class);
        System.out.println("Step 1: Opened /register");

        // 2. フォーム入力を段階的に実行
        registerPage.enterUsername("debuguser");
        System.out.println("Step 2: Entered username");
        System.out.println("  Username field value: " + $("#username").getValue());

        registerPage.enterEmail("debuguser@example.com");
        System.out.println("Step 3: Entered email");
        System.out.println("  Email field value: " + $("#email").getValue());

        registerPage.enterPassword("password123");
        System.out.println("Step 4: Entered password");

        registerPage.enterPasswordConfirm("password123");
        System.out.println("Step 5: Entered password confirm");

        // 3. 送信前のURLを確認
        System.out.println("Step 6: URL before submit: " + url());

        // 4. フォーム送信
        registerPage.clickRegister();
        System.out.println("Step 7: Clicked register button");

        // 5. 送信後、少し待機
        sleep(2000);

        // 6. 現在のURLを出力
        System.out.println("Step 8: URL after submit: " + url());

        // 7. ページソースを確認
        String pageSource = webdriver().driver().source();
        System.out.println("Page contains 'ログイン': " + pageSource.contains("ログイン"));
        System.out.println("Page contains '登録が完了しました': " + pageSource.contains("登録が完了しました"));
        System.out.println("Page contains 'error': " + pageSource.contains("error"));
        System.out.println("Page contains 'Whitelabel Error': " + pageSource.contains("Whitelabel Error"));
    }
}
