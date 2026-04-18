package com.saucedemo.pages;

import com.microsoft.playwright.Page;

public class CheckoutPage {
    private final Page page;
    // TODO: check this place after tests
    private final String FIRST_NAME_INPUT = "#first-name";
    private final String LAST_NAME_INPUT = "#last-name";
    private final String POSTAL_CODE_INPUT = "#postal-code";
    private final String CONTINUE_BUTTON = "#continue";
    private final String FINISH_BUTTON = "#finish";

    public CheckoutPage(Page page) {
        this.page = page;
    }

    public void fillShippingInfo(String firstName, String lastName, String postalCode) {
        page.fill(FIRST_NAME_INPUT, firstName);
        page.fill(LAST_NAME_INPUT, lastName);
        page.fill(POSTAL_CODE_INPUT, postalCode);
        page.click(CONTINUE_BUTTON);
    }

    public void completePurchase() {
        page.click(FINISH_BUTTON);
    }

    public boolean isOrderConfirmed() {
        return page.isVisible(".complete-header");
    }

    public String getConfirmationMessage() {
        return page.textContent(".complete-header");
    }
}
