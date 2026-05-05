package com.saucedemo.tests.ui;

import com.saucedemo.config.SauceDemoTestConfig;
import com.saucedemo.pages.InventoryItemPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.tests.base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PerformanceGlitchUserTest extends BaseTest {

    private static final SauceDemoTestConfig CFG = SauceDemoTestConfig.get();
    private static final String USER_NAME = CFG.performanceGlitchUser();
    private static final String PASSWORD = CFG.standardPassword();

    @Test
    @DisplayName("successful authorization")
    void successfulLoginTest() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
    }

    @Test
    @DisplayName("PerformanceGlitchUser: Navigation: inventory → товар → inventory ")
    void navigationTest() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage currentPage = new InventoryPage(page);
        currentPage.navigateToInventoryItemPageByName(expectedProductList.getFirst().getName());

        InventoryItemPage inventoryPage = new InventoryItemPage(page);
        inventoryPage.navigateToInventoryPage();
        assertTrue(
                page.url().contains("/inventory.html"),
                () -> "Expected https://www.saucedemo.com/inventory.html, got: " + page.url());
    }

    @Test
    @DisplayName("(Expected to fail and uncover a bug.) PerformanceGlitchUser: JUnit should get time-out message")
    void navigationTimeOutTest() {
        assertTimeout(Duration.ofSeconds(10), () -> {
            LoginPage loginPage = new LoginPage(page);
            loginPage.login(USER_NAME, PASSWORD);
            assertTrue(page.url().contains("/inventory.html"));
            InventoryPage currentPage = new InventoryPage(page);
            currentPage.navigateToInventoryItemPageByName(expectedProductList.getFirst().getName());

            InventoryItemPage inventoryPage = new InventoryItemPage(page);
            inventoryPage.navigateToInventoryPage();
            assertTrue(
                    page.url().contains("/inventory.html"),
                    () -> "Expected https://www.saucedemo.com/inventory.html, got: " + page.url());
        }, "Navigating through 4 pages took more than 10 seconds!");
    }
}
