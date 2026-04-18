package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.saucedemo.models.Product;
import com.saucedemo.utils.ProductImageNameNormalizer;

public class InventoryItemPage {

    private final Page page;

    public InventoryItemPage(Page page) {
        this.page = page;
        if (!page.url().contains("/inventory-item.html")) {
            page.waitForURL(
                    url -> url.contains("/inventory-item.html"),
                    new Page.WaitForURLOptions().setTimeout(10_000));
        }
        page.waitForSelector("[data-test='inventory-item-price']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10_000));
    }

    public Product getProduct() {
        return new Product(
                page.locator("[data-test='inventory-item-name']").first().textContent(),
                page.locator("[data-test='inventory-item-desc']").first().textContent(),
                Double.parseDouble(
                        page.locator("[data-test='inventory-item-price']").first()
                                .textContent().replace("$", "")
                ),
                ProductImageNameNormalizer.normalize(
                    page.locator("[data-test='inventory-item'] img").first().getAttribute("src"))
        );
    }

    public void addToCartOnDetailPage() {
        var button = page.locator("[data-test='add-to-cart']");
        button.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(15000));
        button.click(new Locator.ClickOptions().setForce(true).setTimeout(15_000));
    }

    public void removeProductFromCart(String productName) {
        page.locator(String.format(
                        "div.inventory_item:has-text('%s') button:has-text('Remove')",
                        productName))
                .click();
    }

    public int getCartItemCount() {
        try {
            return Integer.parseInt(page.textContent("[data-test='shopping-cart-badge']"));
        } catch (Exception e) {
            return -1;
        }
    }

    public void navigateToInventoryPage() {
        page.locator("[data-test='back-to-products']").click();
        page.waitForURL(
                url -> url.contains("/inventory.html") && !url.contains("inventory-item"),
                new Page.WaitForURLOptions().setTimeout(10_000));
    }
}
