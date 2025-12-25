package com.chirper.frontend.e2e;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * E2Eテストの基底クラス
 *
 * Selenide設定とログインヘルパーメソッドを提供します。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseE2ETest {

    protected static final String TEST_USERNAME = "testuser";
    protected static final String TEST_PASSWORD = "password123";

    @LocalServerPort
    protected int port;

    @BeforeAll
    static void setUpSelenide() {
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.timeout = 10000;
    }

    /**
     * ログインを実行します
     */
    protected void login() {
        login(TEST_USERNAME, TEST_PASSWORD);
    }

    /**
     * 指定した認証情報でログインを実行します
     *
     * @param username ユーザー名
     * @param password パスワード
     */
    protected void login(String username, String password) {
        open("http://localhost:" + port + "/login");
        $("#username").setValue(username);
        $("#password").setValue(password);
        $("button[type=submit]").click();
    }

    /**
     * ログインしてタイムラインページを開きます
     */
    protected void loginAndOpenTimeline() {
        login();
        $("h1").shouldHave(text("タイムライン"));
    }
}
