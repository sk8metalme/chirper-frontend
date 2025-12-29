package com.chirper.e2e.tests;

import com.chirper.e2e.config.SelenideConfig;
import com.chirper.e2e.config.TestDataFixture;
import com.chirper.e2e.pages.LoginPage;
import com.chirper.e2e.pages.TimelinePage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@Tag("e2e")
@DisplayName("ツイートフローE2Eテスト")
class TweetFlowTest {

    @BeforeAll
    static void setup() {
        SelenideConfig.setup();
    }

    @BeforeEach
    void login() {
        LoginPage loginPage = open("/login", LoginPage.class);
        loginPage.loginAs(
            TestDataFixture.Users.TEST_USER_1_USERNAME,
            TestDataFixture.Users.TEST_USER_1_PASSWORD
        );
    }

    @Test
    @DisplayName("ツイート投稿→タイムライン表示→いいね")
    void testPostTweetAndLike() {
        TimelinePage timelinePage = open("/timeline", TimelinePage.class);

        // 1. ツイート投稿
        String tweetContent = TestDataFixture.Tweets.generateTweetContent();
        timelinePage.postTweet(tweetContent);

        // 2. タイムラインに表示されることを確認
        timelinePage.shouldHaveTweet(tweetContent);

        // 3. いいねボタンをクリック
        timelinePage.likeTweet(0);

        // 4. いいね数が1に増加
        timelinePage.shouldShowLikeCount(0, 1);
    }

    @Test
    @DisplayName("ツイート削除")
    void testDeleteTweet() {
        TimelinePage timelinePage = open("/timeline", TimelinePage.class);

        // 1. ツイート投稿
        String tweetContent = "Tweet to be deleted";
        timelinePage.postTweet(tweetContent);

        // 2. 投稿されたことを確認
        timelinePage.shouldHaveTweet(tweetContent);

        // 3. 削除
        int initialCount = timelinePage.getTweetCount();
        timelinePage.deleteTweet(0);

        // 4. ツイート数が減少
        timelinePage.shouldHaveTweetCount(initialCount - 1);
    }
}
