package com.chirper.frontend.e2e;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * ツイート投稿フローのE2Eテスト
 *
 * 注: このテストはバックエンドAPIが稼働している必要があります。
 * 現時点ではバックエンドAPIがモックされていないため、テストは@Disabledでスキップされています。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Disabled("バックエンドAPIの統合後に有効化")
class TweetE2ETest {

    @LocalServerPort
    private int port;

    @BeforeAll
    static void setUpAll() {
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.timeout = 10000;
    }

    @Test
    void shouldSubmitTweetSuccessfully() {
        // Given - ログイン済みの状態でタイムラインページを開く
        loginAndOpenTimeline();

        // When - ツイートを投稿
        $("#tweetContent").setValue("これはテストツイートです");
        $("button[type=submit]").click();

        // Then - 成功メッセージが表示される
        $(".success").shouldHave(text("ツイートを投稿しました"));

        // And - タイムラインにツイートが表示される
        $(".tweet-content").shouldHave(text("これはテストツイートです"));
    }

    @Test
    void shouldShowErrorOnEmptyTweet() {
        // Given - ログイン済みの状態でタイムラインページを開く
        loginAndOpenTimeline();

        // When - 空のツイートを投稿
        $("#tweetContent").setValue("");
        $("button[type=submit]").click();

        // Then - エラーメッセージが表示される
        $(".error").shouldHave(text("ツイート内容に誤りがあります"));
    }

    @Test
    void shouldShowErrorOnTooLongTweet() {
        // Given - ログイン済みの状態でタイムラインページを開く
        loginAndOpenTimeline();

        // When - 280文字を超えるツイートを投稿
        String longTweet = "a".repeat(281);
        $("#tweetContent").setValue(longTweet);
        $("button[type=submit]").click();

        // Then - エラーメッセージが表示される
        $(".error").shouldHave(text("ツイート内容に誤りがあります"));
    }

    @Test
    void shouldDeleteTweetSuccessfully() {
        // Given - ログイン済みの状態でタイムラインページを開く
        loginAndOpenTimeline();

        // And - ツイートを投稿
        $("#tweetContent").setValue("削除テストツイート");
        $("button[type=submit]").click();
        $(".tweet-content").shouldHave(text("削除テストツイート"));

        // When - ツイートを削除
        $(".delete-tweet-button").click();

        // Then - 成功メッセージが表示される
        $(".success").shouldHave(text("ツイートを削除しました"));

        // And - ツイートがタイムラインから消える
        $(".tweet-content").shouldNotHave(text("削除テストツイート"));
    }

    /**
     * ログインしてタイムラインページを開くヘルパーメソッド
     */
    private void loginAndOpenTimeline() {
        open("http://localhost:" + port + "/login");
        $("#username").setValue("testuser");
        $("#password").setValue("password123");
        $("button[type=submit]").click();
        $("h1").shouldHave(text("タイムライン"));
    }
}
