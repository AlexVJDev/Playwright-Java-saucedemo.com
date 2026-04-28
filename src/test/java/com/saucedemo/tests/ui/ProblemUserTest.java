package com.saucedemo.tests.ui;

import com.saucedemo.config.SauceDemoTestConfig;
import com.saucedemo.models.Product;
import com.saucedemo.pages.InventoryItemPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.tests.base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProblemUserTest extends BaseTest {

    private static final SauceDemoTestConfig CFG = SauceDemoTestConfig.get();
    private static final String USER_NAME = CFG.problemUser();
    private static final String PASSWORD = CFG.standardPassword();

    @Test
    @DisplayName("(Expected to fail and uncover a bug.) ProblemUser. inventory page should have correct image-name")
    void inventoryPageShouldHaveCorrectImageName() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage inventoryPage = new InventoryPage(page);
        var productList = inventoryPage.getAllProductList();
        assertEquals(expectedProductList.getFirst().getImageName(),
                productList.getFirst().getImageName(),
                String.format("wrongImageName: expected image name: %s but item page shows %s",
                                productList.getFirst().getImageName(), expectedProductList.getFirst().getImageName()));
    }

    @Test
    @DisplayName("(Expected to fail and uncover a bug.) ProblemUser. Inventory-page should get correct total items number in the cart")
    void inventoryPageShouldHaveCorrectItemsCount() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        InventoryPage inventoryPage = new InventoryPage(page);
        for (Product product : expectedProductList) {
            inventoryPage.addProductToCart(product.getName());
        }
        assertEquals(expectedProductList.size(),
                inventoryPage.getCartItemCount(),
                String.format("problem_user: Wrong total items number in the cart: %s instead of %s",
                            inventoryPage.getCartItemCount(), expectedProductList.size()));
    }

    @Test
    @DisplayName("(Expected to fail and uncover a bug.) ProblemUser. The product is not removed from the shopping list in inventoryPage")
    void inventoryPageShouldWorkRemoveButton() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage inventoryPage = new InventoryPage(page);
        String firstProductName = expectedProductList.getFirst().getName();

        inventoryPage.addProductToCart(firstProductName);
        assertEquals(1, inventoryPage.getCartItemCount(), "After add: cart badge should be 1");

        inventoryPage.removeProductFromCart(firstProductName);
        assertEquals(
                0,
                inventoryPage.getCartItemCount(),
                "problem_user: Remove on inventory Should decrease cart count");
    }

    @Test
    @DisplayName("(Expected to fail and uncover a bug.) ProblemUser. Price on item detail page not match expected catalog price")
    void inventoryItemPageShouldHaveCorrectPrice() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage inventoryPage = new InventoryPage(page);
        inventoryPage.navigateToInventoryItemPageByName(expectedProductList.getFirst().getName());

        InventoryItemPage itemPage = new InventoryItemPage(page);
        Product inventoryItem = itemPage.getProduct();
        assertEquals(
                expectedProductList.getFirst().getPrice(),
                inventoryItem.getPrice(),
                "problem_user: price on item detail should match expected catalog price");
    }

    @Test
    @DisplayName("(Expected to fail and uncover a bug.) ProblemUser. Price on item detail page not match expected catalog name")
    void inventoryItemPageShouldHaveCorrectName() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage inventoryPage = new InventoryPage(page);
        inventoryPage.navigateToInventoryItemPageByName(expectedProductList.getFirst().getName());

        InventoryItemPage itemPage = new InventoryItemPage(page);
        Product inventoryItem = itemPage.getProduct();
        assertEquals(
                expectedProductList.getFirst().getName(),
                inventoryItem.getName(),
                "problem_user: Name on item detail page should match expected catalog price");
    }

    @Test
    @DisplayName("(Expected to fail and uncover a bug.) ProblemUser: Add to cart on inventory-item page must increase cart badge")
    void inventoryItemPageShouldWorkRemove() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);

        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage inventoryPage = new InventoryPage(page);
        inventoryPage.navigateToInventoryItemPageByName(expectedProductList.getFirst().getName());

        InventoryItemPage itemPage = new InventoryItemPage(page);
        itemPage.addToCartOnDetailPage();
        int cartCountAfterAdd = itemPage.getCartItemCount();
        assertEquals(
                1,
                cartCountAfterAdd,
                "problem_user: Add to cart on inventory-item page didn't increase cart badge");
    }
}
