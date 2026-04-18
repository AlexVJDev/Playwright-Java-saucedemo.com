package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.saucedemo.models.Product;
import com.saucedemo.utils.ProductImageNameNormalizer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InventoryPage {
    private final Page page;

    public InventoryPage(Page page) {
        this.page = page;
        page.waitForSelector("[data-test='inventory-list']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10_000));
    }

    public Product getFirstProduct() {
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

    public List<Product> getAllProductList() {
        List<Product> productList = new ArrayList<>();
        int count = page.locator("[data-test='inventory-item']").count();
        for (int i = 0; i < count; i++) {
            productList.add(new Product(
                    page.locator("[data-test='inventory-item-name']").nth(i).textContent(),
                    page.locator("[data-test='inventory-item-desc']").nth(i).textContent(),
                    Double.parseDouble(
                            page.locator("[data-test='inventory-item-price']").nth(i)
                                    .textContent().replace("$", "")
                    ),
                    ProductImageNameNormalizer.normalize(
                            page.locator("[data-test='inventory-item'] img").nth(i).getAttribute("src"))
            ));
        }
        return productList;
    }

    public void setInventoryPageOrder(String orderValue) {
        page.locator("[data-test='product-sort-container']").selectOption(orderValue);
    }

    public List<Double> getAllPricesByOrder() {
        LinkedList<Double> pricesByOrder = new LinkedList<>();
        int count = page.locator("[data-test='inventory-item']").count();
        for (int i = 0; i < count; i++) {
            pricesByOrder.add(
                    Double.parseDouble(
                            page.locator("[data-test='inventory-item-price']").nth(i)
                                    .textContent().replace("$", "")
                    )
            );
        }
        return pricesByOrder;
    }

    public void addProductToCart(String productName) {
        page.locator(String.format(
                        "div.inventory_item:has-text('%s') button:has-text('Add to cart')",
                        productName))
                .click();
    }

    public void removeProductFromCart(String productName) {
        page.locator(String.format(
                        "div.inventory_item:has-text('%s') button:has-text('Remove')",
                        productName))
                .click();
    }

    public int getCartItemCount() {
        try {
            return Integer.parseInt(page.textContent(".shopping_cart_badge"));
        } catch (Exception e) {
            return -1;
        }
    }

    public void navigateToInventoryItemPageByName(String productName) {
        String name = productName.trim();
        Locator titleLink = page.locator("a[data-test$='-title-link']").filter(
                new Locator.FilterOptions().setHasText(name));
        titleLink.click();
        page.waitForURL(
                url -> url.contains("/inventory-item.html"),
                new Page.WaitForURLOptions().setTimeout(10_000));
    }
}
