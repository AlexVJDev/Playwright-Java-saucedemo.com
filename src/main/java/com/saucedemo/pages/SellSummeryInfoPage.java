package com.saucedemo.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.saucedemo.models.SellSummeryInfo;

public class SellSummeryInfoPage {
    private final Page page;

    public SellSummeryInfoPage(Page page) {
        this.page = page;
        page.waitForSelector("[data-test='checkout-summary-container']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10_000));
    }

    public SellSummeryInfo getSellSummeryInfo() {
        return new SellSummeryInfo(
                page.locator("[data-test='payment-info-value']").textContent().trim(),
                page.locator("[data-test='shipping-info-value']").textContent().trim(),
                parseMoneyAfterDollar(page.locator("[data-test='subtotal-label']").textContent()),
                parseMoneyAfterDollar(page.locator("[data-test='tax-label']").textContent()),
                parseMoneyAfterDollar(page.locator("[data-test='total-label']").textContent())
        );
    }

    private static double parseMoneyAfterDollar(String labelText) {
        int dollar = labelText.lastIndexOf('$');
        if (dollar < 0) {
            throw new IllegalArgumentException("Expected currency in: " + labelText);
        }
        return Double.parseDouble(labelText.substring(dollar + 1).trim());
    }

}
