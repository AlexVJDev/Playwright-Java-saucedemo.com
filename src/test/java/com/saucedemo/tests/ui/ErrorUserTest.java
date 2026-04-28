package com.saucedemo.tests.ui;

import org.junit.jupiter.api.DisplayName;
import com.saucedemo.config.SauceDemoTestConfig;
import com.saucedemo.models.Product;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.tests.base.BaseTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ErrorUserTest extends BaseTest {

    private static final SauceDemoTestConfig CFG = SauceDemoTestConfig.get();
    private static final String USER_NAME = CFG.errorUser();
    private static final String PASSWORD = CFG.standardPassword();

    @Test
    @DisplayName("successful authorization")
    void successfulLoginTest() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
    }

    @Test
    @DisplayName("(Expected to fail and uncover a bug.) InventoryPage should have correct cart items count in cart")
    void inventoryPageShouldHaveCorrectCartItemsCount() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        InventoryPage inventoryPage = new InventoryPage(page);
        for (Product product : expectedProductList) {
            inventoryPage.addProductToCart(product.getName());
        }
        assertEquals(expectedProductList.size(),
                inventoryPage.getCartItemCount(),
                String.format("ErrorUserTest: wrong items in cart: expected : %s, but got: %s",
                    expectedProductList.size(), inventoryPage.getCartItemCount()));
    }
}
