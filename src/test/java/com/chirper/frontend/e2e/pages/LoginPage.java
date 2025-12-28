package com.chirper.frontend.e2e.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

/**
 * ログインページの Page Object
 *
 * <p>ログイン画面のUI要素と操作を抽象化します。</p>
 *
 * <h3>使用例:</h3>
 * <pre>{@code
 * LoginPage loginPage = open("/login", LoginPage.class);
 * TimelinePage timeline = loginPage.loginAs("testuser", "password123");
 * }</pre>
 */
public class LoginPage extends BasePage {

    // Page Elements
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement loginButton = $("#login-button, button[type='submit']");
    private final SelenideElement registerLink = $("#register-link, a[href='/register']");

    /**
     * ログインページを開く
     *
     * @return this（メソッドチェーン用）
     */
    public LoginPage open() {
        openPage("/login");
        return this;
    }

    /**
     * ユーザー名を入力
     *
     * @param username ユーザー名
     * @return this（メソッドチェーン用）
     */
    public LoginPage enterUsername(String username) {
        usernameInput.shouldBe(visible).setValue(username);
        return this;
    }

    /**
     * パスワードを入力
     *
     * @param password パスワード
     * @return this（メソッドチェーン用）
     */
    public LoginPage enterPassword(String password) {
        passwordInput.shouldBe(visible).setValue(password);
        return this;
    }

    /**
     * ログインボタンをクリック（成功を期待）
     *
     * @return タイムラインページ
     */
    public TimelinePage clickLogin() {
        loginButton.shouldBe(visible, enabled).click();
        return page(TimelinePage.class);
    }

    /**
     * ログインボタンをクリック（失敗を期待）
     *
     * @return this（メソッドチェーン用、エラーメッセージ検証のため）
     */
    public LoginPage clickLoginExpectingError() {
        loginButton.shouldBe(visible, enabled).click();
        return this;
    }

    /**
     * 登録リンクをクリック
     *
     * @return 登録ページ
     */
    public RegisterPage clickRegister() {
        registerLink.shouldBe(visible).click();
        return page(RegisterPage.class);
    }

    // Verifications

    /**
     * エラーメッセージが表示されることを検証
     *
     * @param expectedMessage 期待されるエラーメッセージ
     * @return this（メソッドチェーン用）
     */
    public LoginPage shouldShowError(String expectedMessage) {
        getErrorMessage().shouldBe(visible).shouldHave(text(expectedMessage));
        return this;
    }

    /**
     * ログインページが表示されていることを検証
     *
     * @return this（メソッドチェーン用）
     */
    public LoginPage shouldBeVisible() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        loginButton.shouldBe(visible);
        return this;
    }

    // Fluent API

    /**
     * ユーザー名とパスワードを入力してログイン（成功を期待）
     *
     * @param username ユーザー名
     * @param password パスワード
     * @return タイムラインページ
     */
    public TimelinePage loginAs(String username, String password) {
        return this.enterUsername(username)
                   .enterPassword(password)
                   .clickLogin();
    }
}
