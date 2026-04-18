package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

public class LoginPage {

    private final Page page;
    private final String URL = "https://www.saucedemo.com/";

    public LoginPage(Page page) {
        this.page = page;
        page.navigate(URL);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    public void submitLoginForm(String username, String password) {
        page.locator("input[data-test='username']").waitFor();
        page.locator("input[data-test='password']").waitFor();

        page.fill("input[data-test='username']", username);
        page.fill("input[data-test='password']", password == null ? "" : password);
        page.click("input[data-test='login-button']");
    }

    public String getErrorMessage() {
        Locator error = page.locator("[data-test='error']");
        error.waitFor();
        String text = error.textContent();
        return text == null ? "" : text.trim();
    }

    public void login(String username, String password) {
        submitLoginForm(username, password);
        page.waitForURL(
                url -> url.contains("/inventory.html"),
                new Page.WaitForURLOptions().setTimeout(10_000)
        );
    }
}
