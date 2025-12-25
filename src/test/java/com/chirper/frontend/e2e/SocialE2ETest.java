package com.chirper.frontend.e2e;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

/**
 * ソーシャル機能（フォロー・アンフォロー）のE2Eテスト
 *
 * 注: このテストはバックエンドAPIが稼働している必要があります。
 * 現時点ではバックエンドAPIがモックされていないため、テストは@Disabledでスキップされています。
 */
@Disabled("バックエンドAPIの統合後に有効化")
class SocialE2ETest extends BaseE2ETest {

    @Test
    void shouldFollowUserSuccessfully() {
        // Given - ログイン済みの状態で他のユーザーのプロフィールページを開く
        login();
        open("http://localhost:" + port + "/profile/otheruser");

        // When - フォローボタンをクリック
        $(".follow-button").click();

        // Then - 成功メッセージが表示される
        $(".success").shouldHave(text("フォローしました"));

        // And - フォローボタンがアンフォローボタンに変わる
        $(".unfollow-button").shouldBe(visible);
    }

    @Test
    void shouldUnfollowUserSuccessfully() {
        // Given - ログイン済みの状態で既にフォロー中のユーザーのプロフィールページを開く
        login();
        open("http://localhost:" + port + "/profile/followeduser");

        // When - アンフォローボタンをクリック
        $(".unfollow-button").click();

        // Then - 成功メッセージが表示される
        $(".success").shouldHave(text("フォローを解除しました"));

        // And - アンフォローボタンがフォローボタンに変わる
        $(".follow-button").shouldBe(visible);
    }

    @Test
    void shouldDisplayFollowersListWithFollowButtons() {
        // Given - ログイン済みの状態でフォロワー一覧ページを開く
        login();
        open("http://localhost:" + port + "/followers/testuser");

        // Then - フォロワーリストが表示される
        $("h1").shouldHave(text("testuser のフォロワー"));
        $(".user-list").shouldBe(visible);

        // And - 各ユーザーにフォローボタンが表示される（自分自身を除く）
        $(".follow-button").shouldBe(visible);
    }

    @Test
    void shouldDisplayFollowingListWithUnfollowButtons() {
        // Given - ログイン済みの状態でフォロー中一覧ページを開く
        login();
        open("http://localhost:" + port + "/following/testuser");

        // Then - フォロー中リストが表示される
        $("h1").shouldHave(text("testuser がフォロー中"));
        $(".user-list").shouldBe(visible);

        // And - 各ユーザーにアンフォローボタンが表示される
        $(".unfollow-button").shouldBe(visible);
    }

    @Test
    void shouldFollowFromUserListInFollowersPage() {
        // Given - ログイン済みの状態でフォロワー一覧ページを開く
        login();
        open("http://localhost:" + port + "/followers/otheruser");

        // When - フォローボタンをクリック
        $$(".follow-button").first().click();

        // Then - 成功メッセージが表示される
        $(".success").shouldHave(text("フォローしました"));

        // And - ボタンがアンフォローボタンに変わる
        $$(".unfollow-button").first().shouldBe(visible);
    }

    @Test
    void shouldUnfollowFromUserListInFollowingPage() {
        // Given - ログイン済みの状態でフォロー中一覧ページを開く
        login();
        open("http://localhost:" + port + "/following/testuser");

        // When - アンフォローボタンをクリック
        $$(".unfollow-button").first().click();

        // Then - 成功メッセージが表示される
        $(".success").shouldHave(text("フォローを解除しました"));

        // And - ボタンがフォローボタンに変わる
        $$(".follow-button").first().shouldBe(visible);
    }
}
