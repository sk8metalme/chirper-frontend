package com.chirper.frontend.e2e;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * プロフィール表示・編集フローのE2Eテスト
 *
 * 注: このテストはバックエンドAPIが稼働している必要があります。
 * 現時点ではバックエンドAPIがモックされていないため、テストは@Disabledでスキップされています。
 */
@Disabled("バックエンドAPIの統合後に有効化")
class ProfileE2ETest extends BaseE2ETest {

    @Test
    void shouldDisplayUserProfile() {
        // Given - ログイン済みの状態
        login();

        // When - プロフィールページを開く
        open("http://localhost:" + port + "/profile/testuser");

        // Then - プロフィール情報が表示される
        $(".profile-username").shouldHave(text("testuser"));
        $(".profile-bio").shouldHave(text("テストユーザーの自己紹介"));
        $(".follower-count").shouldHave(text("10"));
        $(".following-count").shouldHave(text("5"));
    }

    @Test
    void shouldEditProfileSuccessfully() {
        // Given - ログイン済みの状態でプロフィール編集ページを開く
        login();
        open("http://localhost:" + port + "/profile/edit");

        // When - プロフィールを編集
        $("#displayName").clear();
        $("#displayName").setValue("新しい表示名");
        $("#bio").clear();
        $("#bio").setValue("新しい自己紹介文");
        $("button[type=submit]").click();

        // Then - 成功メッセージが表示される
        $(".success").shouldHave(text("プロフィールを更新しました"));

        // And - プロフィールページにリダイレクトされる
        $(".profile-username").shouldHave(text("testuser"));
        $(".profile-bio").shouldHave(text("新しい自己紹介文"));
    }

    @Test
    void shouldShowErrorOnTooLongBio() {
        // Given - ログイン済みの状態でプロフィール編集ページを開く
        login();
        open("http://localhost:" + port + "/profile/edit");

        // When - 160文字を超える自己紹介文を入力
        String longBio = "a".repeat(161);
        $("#bio").clear();
        $("#bio").setValue(longBio);
        $("button[type=submit]").click();

        // Then - エラーメッセージが表示される
        $(".error").shouldHave(text("自己紹介は160文字以内で入力してください"));
    }

    @Test
    void shouldDisplayOtherUserProfile() {
        // Given - ログイン済みの状態
        login();

        // When - 他のユーザーのプロフィールページを開く
        open("http://localhost:" + port + "/profile/otheruser");

        // Then - プロフィール情報が表示される
        $(".profile-username").shouldHave(text("otheruser"));
        // And - 編集ボタンは表示されない
        $(".edit-profile-button").shouldNotBe(visible);
    }

    @Test
    void shouldDisplayFollowersList() {
        // Given - ログイン済みの状態
        login();

        // When - フォロワー一覧ページを開く
        open("http://localhost:" + port + "/followers/testuser");

        // Then - フォロワーリストが表示される
        $("h1").shouldHave(text("testuser のフォロワー"));
        $(".user-list").shouldBe(visible);
    }

    @Test
    void shouldDisplayFollowingList() {
        // Given - ログイン済みの状態
        login();

        // When - フォロー中一覧ページを開く
        open("http://localhost:" + port + "/following/testuser");

        // Then - フォロー中リストが表示される
        $("h1").shouldHave(text("testuser がフォロー中"));
        $(".user-list").shouldBe(visible);
    }
}
