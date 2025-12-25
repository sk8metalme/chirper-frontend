package com.chirper.frontend.e2e;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * ログイン・ログアウトフローのE2Eテスト
 *
 * 注: このテストはバックエンドAPIが稼働している必要があります。
 * 現時点ではバックエンドAPIがモックされていないため、テストは@Disabledでスキップされています。
 */
@Disabled("バックエンドAPIの統合後に有効化")
class LoginE2ETest extends BaseE2ETest {

    @Test
    void shouldLoginAndLogoutSuccessfully() {
        // Given - ログインページを開く
        open("http://localhost:" + port + "/login");

        // When - 有効な認証情報でログイン
        $("#username").setValue("testuser");
        $("#password").setValue("password123");
        $("button[type=submit]").click();

        // Then - タイムラインページにリダイレクトされる
        $("h1").shouldHave(text("タイムライン"));

        // When - ログアウト
        $("a[href='/logout']").click();

        // Then - ホームページにリダイレクトされる
        $("h1").shouldHave(text("Chirper"));
    }

    @Test
    void shouldShowErrorOnInvalidLogin() {
        // Given - ログインページを開く
        open("http://localhost:" + port + "/login");

        // When - 無効な認証情報でログイン
        $("#username").setValue("invaliduser");
        $("#password").setValue("wrongpassword");
        $("button[type=submit]").click();

        // Then - エラーメッセージが表示される
        $(".error").shouldHave(text("ログインに失敗しました"));
    }

    @Test
    void shouldRedirectToLoginWhenNotAuthenticated() {
        // Given - 認証されていない状態

        // When - 認証が必要なページにアクセス
        open("http://localhost:" + port + "/timeline");

        // Then - ログインページにリダイレクトされる
        $("h1").shouldHave(text("ログイン"));
    }
}
