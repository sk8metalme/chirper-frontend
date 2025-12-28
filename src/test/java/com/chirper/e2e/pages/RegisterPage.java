package com.chirper.e2e.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class RegisterPage extends BasePage {

    // Page Elements
    private SelenideElement usernameInput = $("#username");
    private SelenideElement emailInput = $("#email");
    private SelenideElement passwordInput = $("#password");
    private SelenideElement passwordConfirmInput = $("#passwordConfirm");
    private SelenideElement registerButton = $("button[type='submit']");

    // Actions
    public RegisterPage open() {
        openPage("/register");
        return this;
    }

    public RegisterPage enterUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public RegisterPage enterEmail(String email) {
        emailInput.setValue(email);
        return this;
    }

    public RegisterPage enterPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage enterPasswordConfirm(String passwordConfirm) {
        passwordConfirmInput.setValue(passwordConfirm);
        return this;
    }

    public RegisterPage clickRegister() {
        // Use submit() instead of click() for better headless compatibility
        $("form[action='/register']").submit();
        return this;
    }

    public LoginPage navigateToLogin() {
        return page(LoginPage.class);
    }

    // Verifications
    // Note: register.html has no success alert - success message appears on login page after redirect
    public RegisterPage shouldShowError(String message) {
        $(".alert-danger").shouldBe(visible).shouldHave(text(message));
        return this;
    }

    // Fluent API
    public RegisterPage registerAs(String username, String email, String password) {
        return this.enterUsername(username)
                   .enterEmail(email)
                   .enterPassword(password)
                   .enterPasswordConfirm(password)  // Same password for confirmation
                   .clickRegister();
    }
}
