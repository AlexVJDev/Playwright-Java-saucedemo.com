package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.models.CartItem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartPage {
    private final Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public void navigateToCart() {
        page.click(".shopping_cart_link");
    }

    public void removeItem(String productName) {
        page.locator(".cart_item:has-text('" + productName + "') button").click();
        page.waitForCondition(
                () -> !page.isVisible(".cart_item:has-text('" + productName + "')"),
                new Page.WaitForConditionOptions().setTimeout(5000)
        );
    }

    public List<String> getCartItems() {
        return page.locator(".inventory_item_name").allTextContents();
    }

    public void proceedToCheckout() {
        page.click("button[data-test='checkout']");
    }

    public BigDecimal getTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (String text : page.locator(
                "[data-test='inventory-item'] [data-test='inventory-item-price']"
        ).allTextContents()) {
            String numeric = text.replace("$", "").trim();
            total = total.add(new BigDecimal(numeric));
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public List<CartItem> getAllCartItemList() {
        List<CartItem> cartItemListList = new ArrayList<>();
        Locator rows = page.locator("[data-test='inventory-item']");
        int count = rows.count();
        for (int i = 0; i < count; i++) {
            Locator row = rows.nth(i);
            cartItemListList.add(new CartItem(
                    Integer.parseInt(row.locator("[data-test='item-quantity']").textContent().trim()),
                    row.locator("[data-test='inventory-item-name']").textContent(),
                    row.locator("[data-test='inventory-item-desc']").textContent(),
                    Double.parseDouble(
                            row.locator("[data-test='inventory-item-price']")
                                    .textContent().replace("$", "").trim()
                    )
            ));
        }
        return cartItemListList;
    }

    public void removeProductFromCart(String productName) {
        String removeStr = "[data-test='remove-" + productName.replaceAll("\\s+", "-").toLowerCase(Locale.ROOT)  + "']";
        Locator removeButton = page.locator(removeStr);
        removeButton.click();
        page.waitForCondition(
                () -> !removeButton.isVisible(),
                new Page.WaitForConditionOptions().setTimeout(5000)
        );
    }

    public int getCartItemCount() {
        try {
            return Integer.parseInt(page.textContent(".shopping_cart_badge"));
        } catch (Exception e) {
            return -1;
        }
    }

}
