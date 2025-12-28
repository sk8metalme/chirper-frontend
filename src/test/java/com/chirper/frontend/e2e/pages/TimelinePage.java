package com.chirper.frontend.e2e.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * タイムラインページの Page Object
 *
 * <p>タイムライン画面のUI要素と操作を抽象化します。</p>
 *
 * <h3>使用例:</h3>
 * <pre>{@code
 * TimelinePage timeline = open("/timeline", TimelinePage.class);
 * timeline.postTweet("Hello, Chirper!")
 *         .shouldHaveTweet("Hello, Chirper!")
 *         .likeTweet(0);
 * }</pre>
 */
public class TimelinePage extends BasePage {

    // Page Elements
    private final SelenideElement tweetInput = $("#tweet-input, textarea[name='content']");
    private final SelenideElement postButton = $("#post-button, button[type='submit']");
    private final SelenideElement logoutButton = $("#logout-button, a[href='/logout']");
    private final ElementsCollection tweets = $$(".tweet-item, .tweet");

    /**
     * タイムラインページを開く
     *
     * @return this（メソッドチェーン用）
     */
    public TimelinePage open() {
        openPage("/timeline");
        return this;
    }

    /**
     * ツイートを投稿
     *
     * @param content ツイート内容
     * @return this（メソッドチェーン用）
     */
    public TimelinePage postTweet(String content) {
        tweetInput.shouldBe(visible).setValue(content);
        postButton.shouldBe(visible, enabled).click();
        // 投稿後、入力欄がクリアされるまで待機
        tweetInput.shouldBe(empty);
        return this;
    }

    /**
     * 指定したインデックスのツイートにいいね
     *
     * @param index ツイートのインデックス（0始まり）
     * @return this（メソッドチェーン用）
     */
    public TimelinePage likeTweet(int index) {
        tweets.get(index).$(".like-button, button[data-action='like']")
              .shouldBe(visible, enabled)
              .click();
        return this;
    }

    /**
     * 指定したインデックスのツイートを削除
     *
     * @param index ツイートのインデックス（0始まり）
     * @return this（メソッドチェーン用）
     */
    public TimelinePage deleteTweet(int index) {
        tweets.get(index).$(".delete-button, button[data-action='delete']")
              .shouldBe(visible, enabled)
              .click();
        // 確認ダイアログがある場合は承認（オプション）
        // switchTo().alert().accept();
        return this;
    }

    /**
     * ログアウト
     *
     * @return ログインページ
     */
    public LoginPage logout() {
        logoutButton.shouldBe(visible).click();
        return page(LoginPage.class);
    }

    // Verifications

    /**
     * タイムラインが表示されていることを検証
     *
     * @return this（メソッドチェーン用）
     */
    public TimelinePage shouldBeVisible() {
        tweetInput.shouldBe(visible);
        postButton.shouldBe(visible);
        return this;
    }

    /**
     * 指定した内容のツイートがタイムラインに存在することを検証
     *
     * @param content ツイート内容
     * @return this（メソッドチェーン用）
     */
    public TimelinePage shouldHaveTweet(String content) {
        tweets.shouldHave(CollectionCondition.itemWithText(content));
        return this;
    }

    /**
     * 指定した内容のツイートがタイムラインに存在しないことを検証
     *
     * @param content ツイート内容
     * @return this（メソッドチェーン用）
     */
    public TimelinePage shouldNotHaveTweet(String content) {
        tweets.filter(text(content)).shouldHave(CollectionCondition.size(0));
        return this;
    }

    /**
     * 指定したインデックスのツイートのいいね数を検証
     *
     * @param index ツイートのインデックス（0始まり）
     * @param expectedCount 期待されるいいね数
     * @return this（メソッドチェーン用）
     */
    public TimelinePage shouldShowLikeCount(int index, int expectedCount) {
        tweets.get(index).$(".like-count, .likes")
              .shouldHave(text(String.valueOf(expectedCount)));
        return this;
    }

    /**
     * タイムラインのツイート数を検証
     *
     * @param expectedCount 期待されるツイート数
     * @return this（メソッドチェーン用）
     */
    public TimelinePage shouldHaveTweetCount(int expectedCount) {
        tweets.shouldHave(CollectionCondition.size(expectedCount));
        return this;
    }

    /**
     * モバイルレイアウトが表示されていることを検証
     *
     * @return this（メソッドチェーン用）
     */
    public TimelinePage shouldHaveMobileLayout() {
        $(".mobile-nav, .navbar-mobile").shouldBe(visible);
        return this;
    }

    /**
     * モバイルナビゲーションが表示されていることを検証
     *
     * @return this（メソッドチェーン用）
     */
    public TimelinePage shouldHaveMobileNavigation() {
        $(".mobile-nav, .bottom-nav").shouldBe(visible);
        return this;
    }
}
