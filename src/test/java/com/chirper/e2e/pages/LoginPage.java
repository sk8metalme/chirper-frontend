package com.chirper.e2e.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class LoginPage extends BasePage {

    // Page Elements
    private SelenideElement usernameInput = $("#username");
    private SelenideElement passwordInput = $("#password");
    private SelenideElement loginButton = $("button[type='submit']");
    private SelenideElement errorMessage = $(".alert-danger");

    // Actions
    public LoginPage open() {
        openPage("/login");
        return this;
    }

    public LoginPage enterUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public TimelinePage clickLogin() {
        // Use submit() instead of click() for better headless compatibility
        $("form[action='/login']").submit();
        return page(TimelinePage.class);
    }

    public LoginPage clickLoginExpectingError() {
        // Use submit() instead of click() for better headless compatibility
        $("form[action='/login']").submit();
        return this;
    }

    // Verifications
    public LoginPage shouldShowError(String message) {
        errorMessage.shouldBe(visible).shouldHave(text(message));
        return this;
    }

    public LoginPage shouldShowSuccess(String message) {
        $(".alert-success").shouldBe(visible).shouldHave(text(message));
        return this;
    }

    // Fluent API: Login in one go
    public TimelinePage loginAs(String username, String password) {
        return this.enterUsername(username)
                   .enterPassword(password)
                   .clickLogin();
    }
}
