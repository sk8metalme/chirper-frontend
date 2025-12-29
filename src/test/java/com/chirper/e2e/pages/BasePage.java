package com.chirper.e2e.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public abstract class BasePage {

    protected void openPage(String url) {
        open(url);
    }

    public SelenideElement getHeader() {
        return $("nav.navbar");
    }

    public void logout() {
        $("form[action='/logout'] button[type='submit']").click();
    }

    public boolean isLoggedIn() {
        return $("a.nav-link[href='/timeline']").exists();
    }
}
