package com.chirper.e2e.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ProfilePage extends BasePage {

    // Page Elements
    private SelenideElement followButton = $("#follow-button");
    private SelenideElement unfollowButton = $("#unfollow-button");
    private SelenideElement followStatusButton = $(".follow-status");
    private SelenideElement followersTab = $("#followers-tab");
    private SelenideElement followersList = $("#followers-list");

    // Actions
    public ProfilePage open(String username) {
        openPage("/profile/" + username);
        return this;
    }

    public ProfilePage clickFollowButton() {
        followButton.click();
        return this;
    }

    public ProfilePage clickUnfollowButton() {
        unfollowButton.click();
        return this;
    }

    public ProfilePage openFollowersTab() {
        followersTab.click();
        return this;
    }

    // Verifications
    public ProfilePage shouldShowFollowStatus(String status) {
        followStatusButton.shouldHave(text(status));
        return this;
    }

    public ProfilePage shouldHaveFollower(String username) {
        followersList.$$(".follower-item")
                     .findBy(text(username))
                     .shouldBe(visible);
        return this;
    }
}
