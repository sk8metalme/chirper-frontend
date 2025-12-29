package com.chirper.e2e.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class SearchPage extends BasePage {

    // Page Elements
    private SelenideElement searchInput = $("#search-input");
    private SelenideElement searchButton = $("#search-button");
    private SelenideElement searchResults = $("#search-results");

    // Actions
    public SearchPage open() {
        openPage("/search");
        return this;
    }

    public SearchPage searchUser(String username) {
        searchInput.setValue(username);
        searchButton.click();
        return this;
    }

    public SearchPage followUser(String username) {
        searchResults.$$(".user-item")
                     .findBy(text(username))
                     .$(".follow-button")
                     .click();
        return this;
    }

    public ProfilePage openUserProfile(String username) {
        searchResults.$$(".user-item")
                     .findBy(text(username))
                     .click();
        return page(ProfilePage.class);
    }
}
