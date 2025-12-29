package com.chirper.e2e.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class TimelinePage extends BasePage {

    // Page Elements
    private SelenideElement tweetInput = $("#content");
    private SelenideElement postButton = $("button[type='submit']");
    private ElementsCollection tweets = $$(".card.mb-3");

    // Actions
    public TimelinePage open() {
        openPage("/timeline");
        return this;
    }

    public TimelinePage postTweet(String content) {
        tweetInput.setValue(content);
        postButton.click();
        return this;
    }

    public TimelinePage likeTweet(int index) {
        tweets.get(index).$$("button[type='submit']").first().click();
        return this;
    }

    public TimelinePage deleteTweet(int index) {
        tweets.get(index).$(".btn-outline-danger").click();
        return this;
    }

    // Verifications
    public TimelinePage shouldHaveTweet(String content) {
        tweets.findBy(text(content)).shouldBe(visible);
        return this;
    }

    public TimelinePage shouldHaveTweetCount(int count) {
        tweets.shouldHave(size(count));
        return this;
    }

    public TimelinePage shouldShowLikeCount(int tweetIndex, int likeCount) {
        tweets.get(tweetIndex).$$("button[type='submit']").first()
              .shouldHave(text(String.valueOf(likeCount)));
        return this;
    }

    public int getTweetCount() {
        return tweets.size();
    }
}
