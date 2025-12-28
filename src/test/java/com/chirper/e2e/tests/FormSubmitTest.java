package com.chirper.e2e.tests;

import com.chirper.e2e.config.SelenideConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;

@Tag("e2e")
@DisplayName("フォーム送信デバッグ")
class FormSubmitTest {

    @BeforeAll
    static void setup() {
        SelenideConfig.setup();
    }

    @Test
    @DisplayName("JavaScript経由でフォーム送信")
    void testFormSubmitViaJavaScript() {
        // 1. 登録ページを開く
        open("/register");
        System.out.println("Step 1: Opened /register");

        // 2. フォーム入力
        $("#username").setValue("jsuser");
        $("#email").setValue("jsuser@example.com");
        $("#password").setValue("password123");
        $("#passwordConfirm").setValue("password123");
        System.out.println("Step 2: Filled form fields");

        // 3. JavaScript経由でフォーム送信
        executeJavaScript("document.querySelector('form[action=\"/register\"]').submit()");
        System.out.println("Step 3: Submitted form via JavaScript");

        // 4. 少し待機
        sleep(3000);

        // 5. 現在のURLを確認
        System.out.println("Step 4: URL after submit: " + url());

        // 6. ページ内容を確認
        String pageSource = webdriver().driver().source();
        System.out.println("Page contains 'ログイン': " + pageSource.contains("ログイン"));
        System.out.println("Page contains '登録が完了しました': " + pageSource.contains("登録が完了しました"));
        System.out.println("Page contains 'error': " + pageSource.contains("error"));
        System.out.println("Page contains 'Whitelabel Error': " + pageSource.contains("Whitelabel Error"));

        if (pageSource.contains("error") || pageSource.contains("エラー")) {
            System.out.println("ERROR FOUND IN PAGE");
            // エラー部分を抽出
            int errorIndex = pageSource.toLowerCase().indexOf("error");
            if (errorIndex > 0) {
                int start = Math.max(0, errorIndex - 100);
                int end = Math.min(pageSource.length(), errorIndex + 200);
                System.out.println("Error context: " + pageSource.substring(start, end));
            }
        }
    }
}
