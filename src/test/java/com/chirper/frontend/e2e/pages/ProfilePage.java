package com.chirper.frontend.e2e.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * プロフィールページの Page Object
 *
 * <p>ユーザープロフィール画面のUI要素と操作を抽象化します。</p>
 *
 * <h3>使用例:</h3>
 * <pre>{@code
 * ProfilePage profilePage = open("/profile/testuser", ProfilePage.class);
 * profilePage.clickFollow()
 *            .shouldShowFollowingStatus();
 * }</pre>
 */
public class ProfilePage extends BasePage {

    // Page Elements
    private final SelenideElement profileUsername = $(".profile-username, h1");
    private final SelenideElement followButton = $("#follow-button, button[data-action='follow']");
    private final SelenideElement unfollowButton = $("#unfollow-button, button[data-action='unfollow']");
    private final SelenideElement followerCount = $(".follower-count, .followers");
    private final SelenideElement followingCount = $(".following-count, .following");
    private final SelenideElement profileBio = $(".profile-bio, .bio");

    /**
     * プロフィールページを開く
     *
     * @param username ユーザー名
     * @return this（メソッドチェーン用）
     */
    public ProfilePage open(String username) {
        openPage("/profile/" + username);
        return this;
    }

    /**
     * フォローボタンをクリック
     *
     * @return this（メソッドチェーン用）
     */
    public ProfilePage clickFollow() {
        followButton.shouldBe(visible, enabled).click();
        return this;
    }

    /**
     * アンフォローボタンをクリック
     *
     * @return this（メソッドチェーン用）
     */
    public ProfilePage clickUnfollow() {
        unfollowButton.shouldBe(visible, enabled).click();
        return this;
    }

    // Verifications

    /**
     * プロフィールが表示されていることを検証
     *
     * @param expectedUsername 期待されるユーザー名
     * @return this（メソッドチェーン用）
     */
    public ProfilePage shouldShowProfile(String expectedUsername) {
        profileUsername.shouldBe(visible).shouldHave(text(expectedUsername));
        return this;
    }

    /**
     * フォロー中ステータスが表示されることを検証
     *
     * @return this（メソッドチェーン用）
     */
    public ProfilePage shouldShowFollowingStatus() {
        unfollowButton.shouldBe(visible);
        return this;
    }

    /**
     * フォローしていないステータスが表示されることを検証
     *
     * @return this（メソッドチェーン用）
     */
    public ProfilePage shouldShowNotFollowingStatus() {
        followButton.shouldBe(visible);
        return this;
    }

    /**
     * フォロワー数を検証
     *
     * @param expectedCount 期待されるフォロワー数
     * @return this（メソッドチェーン用）
     */
    public ProfilePage shouldShowFollowerCount(int expectedCount) {
        followerCount.shouldHave(text(String.valueOf(expectedCount)));
        return this;
    }

    /**
     * フォロー数を検証
     *
     * @param expectedCount 期待されるフォロー数
     * @return this（メソッドチェーン用）
     */
    public ProfilePage shouldShowFollowingCount(int expectedCount) {
        followingCount.shouldHave(text(String.valueOf(expectedCount)));
        return this;
    }
}
