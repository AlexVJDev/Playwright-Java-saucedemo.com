package com.saucedemo.tests.ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.BoundingBox;
import com.saucedemo.config.SauceDemoTestConfig;
import com.saucedemo.models.Product;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.tests.base.BaseTest;
import com.saucedemo.utils.InventorySortOrderEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

public class VisualUserTest extends BaseTest {

    private static final SauceDemoTestConfig CFG = SauceDemoTestConfig.get();
    private static final String USER_NAME = CFG.visualUser();
    private static final String PASSWORD = CFG.standardPassword();
    private static final double BUTTON_IN_CARD_TOLERANCE_PX = 2.0;

    @Test
    @DisplayName("successful authorization")
    void successfulLoginTest() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
    }

    @Test
    @DisplayName("(Should fail and reveal a bug.) Inventory: Add to cart buttons must not overflow product cards horizontally.")
    void inventoryPageAddToCartButtonsFitInsideCards() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        new InventoryPage(page);

        Locator cards = page.locator("[data-test='inventory-item']");
        int count = cards.count();
        assertTrue(count > 0, "Expected at least one product card on inventory");

        for (int i = 0; i < count; i++) {
            Locator card = cards.nth(i);
            Locator addButton = card.locator("[data-test^='add-to-cart-']");
            BoundingBox cardBox = card.boundingBox();
            BoundingBox buttonBox = addButton.boundingBox();
            int finalI = i;

            assertNotNull(cardBox, () -> "Product card " + finalI + " should have a bounding box");
            assertNotNull(buttonBox, () -> "Add to cart button on card " + finalI + " should have a bounding box");

            double cardRight = cardBox.x + cardBox.width;
            double buttonRight = buttonBox.x + buttonBox.width;

            assertTrue(
                    buttonRight <= cardRight + BUTTON_IN_CARD_TOLERANCE_PX,
                    () -> String.format(
                            "Card %d: button right (%.2f) is past card right (%.2f); tolerance %.1f px",
                            finalI, buttonRight, cardRight, BUTTON_IN_CARD_TOLERANCE_PX));
        }
    }

    @Test
    @DisplayName("(Should fail and reveal a bug.) Inventory page should have correct image-name")
    void inventoryPageShouldHaveCorrectImageName() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage inventoryPage = new InventoryPage(page);
        var productList = inventoryPage.getAllProductList();
        assertEquals(expectedProductList.getFirst().getImageName(),
                productList.getFirst().getImageName(),
                String.format("wrongImageName: expected image name: %s but item page shows %s",
                        productList.getFirst().getImageName(), expectedProductList.getFirst().getImageName())
        );
    }

    @Test
    @DisplayName("(Should fail and reveal a bug.) Inventory-page should have correct price-list (order by name: A to Z)")
    void inventoryPageShouldHavePriceListOrderByNameAtoZ() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage inventoryPage = new InventoryPage(page);

        inventoryPage.setInventoryPageOrder(InventorySortOrderEnum.NAME_A_TO_Z.value());
        var pricesList = inventoryPage.getAllPricesByOrder();
        var expectedPricesList = expectedProductList.stream()
                .sorted(Comparator.comparing(p -> p.getName().toLowerCase()))
                .map(Product::getPrice)
                .collect(Collectors.toCollection(LinkedList::new));
        assertEquals(expectedPricesList, pricesList
            ,String.format("inventoryPageHaveWrongPrices: wrong prices in Inventory-page. Expected: %s, but got: %s",
                        expectedPricesList, pricesList));
    }
}
