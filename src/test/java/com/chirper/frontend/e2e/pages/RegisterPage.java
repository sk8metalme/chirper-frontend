package com.chirper.frontend.e2e.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

/**
 * ユーザー登録ページの Page Object
 *
 * <p>ユーザー登録画面のUI要素と操作を抽象化します。</p>
 *
 * <h3>使用例:</h3>
 * <pre>{@code
 * RegisterPage registerPage = open("/register", RegisterPage.class);
 * LoginPage loginPage = registerPage.registerAs("newuser", "new@example.com", "password123");
 * }</pre>
 */
public class RegisterPage extends BasePage {

    // Page Elements
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement emailInput = $("#email");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement registerButton = $("#register-button, button[type='submit']");
    private final SelenideElement loginLink = $("#login-link, a[href='/login']");

    /**
     * 登録ページを開く
     *
     * @return this（メソッドチェーン用）
     */
    public RegisterPage open() {
        openPage("/register");
        return this;
    }

    /**
     * ユーザー名を入力
     *
     * @param username ユーザー名
     * @return this（メソッドチェーン用）
     */
    public RegisterPage enterUsername(String username) {
        usernameInput.shouldBe(visible).setValue(username);
        return this;
    }

    /**
     * メールアドレスを入力
     *
     * @param email メールアドレス
     * @return this（メソッドチェーン用）
     */
    public RegisterPage enterEmail(String email) {
        emailInput.shouldBe(visible).setValue(email);
        return this;
    }

    /**
     * パスワードを入力
     *
     * @param password パスワード
     * @return this（メソッドチェーン用）
     */
    public RegisterPage enterPassword(String password) {
        passwordInput.shouldBe(visible).setValue(password);
        return this;
    }

    /**
     * 登録ボタンをクリック
     *
     * @return ログインページ
     */
    public LoginPage clickRegister() {
        registerButton.shouldBe(visible, enabled).click();
        return page(LoginPage.class);
    }

    /**
     * ログインリンクをクリック
     *
     * @return ログインページ
     */
    public LoginPage clickLogin() {
        loginLink.shouldBe(visible).click();
        return page(LoginPage.class);
    }

    // Verifications

    /**
     * 成功メッセージが表示されることを検証
     *
     * @param expectedMessage 期待される成功メッセージ
     * @return this（メソッドチェーン用）
     */
    public RegisterPage shouldShowSuccess(String expectedMessage) {
        getSuccessMessage().shouldBe(visible).shouldHave(text(expectedMessage));
        return this;
    }

    /**
     * エラーメッセージが表示されることを検証
     *
     * @param expectedMessage 期待されるエラーメッセージ
     * @return this（メソッドチェーン用）
     */
    public RegisterPage shouldShowError(String expectedMessage) {
        getErrorMessage().shouldBe(visible).shouldHave(text(expectedMessage));
        return this;
    }

    // Fluent API

    /**
     * ユーザー情報を入力して登録
     *
     * @param username ユーザー名
     * @param email メールアドレス
     * @param password パスワード
     * @return ログインページ
     */
    public LoginPage registerAs(String username, String email, String password) {
        return this.enterUsername(username)
                   .enterEmail(email)
                   .enterPassword(password)
                   .clickRegister();
    }
}
