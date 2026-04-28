package com.saucedemo.tests.ui;

import com.microsoft.playwright.Page;
import com.saucedemo.models.CartItem;
import com.saucedemo.models.Product;
import com.saucedemo.pages.*;
import com.saucedemo.config.SauceDemoTestConfig;
import com.saucedemo.tests.base.BaseTest;
import com.saucedemo.utils.InventorySortOrderEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

public class StandardUserTest extends BaseTest {

    private static final List<String> RESERVED_WORDS = List.of("Test", "()");
    private static final SauceDemoTestConfig CFG = SauceDemoTestConfig.get();
    private static final String USER_NAME = CFG.standardUsername();
    private static final String PASSWORD = CFG.standardPassword();

    @TestTemplate
    @DisplayName("Successful authorization")
    void successfulLoginTest() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
    }

    @TestTemplate
    @DisplayName("Login Expected to fail with empty password.")
    void loginFailedNoPassword() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.submitLoginForm(USER_NAME, null);
        assertFalse(page.url().contains("/inventory.html"), "Must stay on login page when password is empty");
        assertEquals("Epic sadface: Password is required", loginPage.getErrorMessage());
    }

    @TestTemplate
    @DisplayName("Login Expected to fail with wrong password")
    void loginShouldFailWrongPassword() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.submitLoginForm(USER_NAME, "wrongPassword");
        assertFalse(page.url().contains("/inventory.html"), "Must stay on login page when password is wrong");
        assertEquals("Epic sadface: Username and password do not match any user in this service",
                loginPage.getErrorMessage());
    }

    @TestTemplate
    @DisplayName("Should work navigation: login -> inventory -> inventoryItem -> inventory and take less than 2 sec.")
    void navigationTest() {
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
        }, "Navigation took more than 2 seconds!");
    }

    @TestTemplate
    @DisplayName("Inventory-page have correct first product")
    void inventoryPageHaveCorrectFirstProduct() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage inventoryPage = new InventoryPage(page);
        var product = inventoryPage.getFirstProduct();
        assertEquals(expectedProductList.getFirst(), product);
    }

    @TestTemplate
    @DisplayName("Inventory-page should have correct products list")
    void inventoryPageHaveCorrectAllProductList() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage inventoryPage = new InventoryPage(page);
        var productList = inventoryPage.getAllProductList();
        assertEquals(expectedProductList, productList);
    }

    @TestTemplate
    @DisplayName("Inventory-page should have correct price-list. Items order by name: A to Z")
    void inventoryPageHaveCorrectPriceListOrderByNameAtoZ() {
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
        assertEquals(expectedPricesList, pricesList);
    }

    @TestTemplate
    @DisplayName("Inventory-page should have correct price-list. Items order by name: Z to A")
    void inventoryPageHaveCorrectPriceListOrderByNameZtoA() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage inventoryPage = new InventoryPage(page);

        inventoryPage.setInventoryPageOrder(InventorySortOrderEnum.NAME_Z_TO_A.value());
        var pricesList = inventoryPage.getAllPricesByOrder();
        var expectedPricesList = expectedProductList.stream()
                .sorted(Comparator.comparing((Product p) -> p.getName().toLowerCase()).reversed())
                .map(Product::getPrice)
                .collect(Collectors.toCollection(LinkedList::new));
        assertEquals(expectedPricesList, pricesList);
    }

    @TestTemplate
    @DisplayName("Inventory-page should have correct price-list. Items order by price: Lo to Hi")
    void inventoryPageHaveCorrectPriceListOrderByPriceLoToHi() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage inventoryPage = new InventoryPage(page);

        inventoryPage.setInventoryPageOrder(InventorySortOrderEnum.PRICE_LOW_TO_HIGH.value());
        var pricesList = inventoryPage.getAllPricesByOrder();
        var expectedPricesList = expectedProductList.stream()
                .sorted(Comparator.comparingDouble(Product::getPrice))
                .map(Product::getPrice)
                .collect(Collectors.toCollection(LinkedList::new));
        assertEquals(expectedPricesList, pricesList);
    }

    @TestTemplate
    @DisplayName("Inventory-page should have correct price-list. Items order by price: Hi to Lo")
    void inventoryPageHaveCorrectPriceListOrderByPriceHiToLo() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        assertTrue(page.url().contains("/inventory.html"));
        InventoryPage inventoryPage = new InventoryPage(page);

        inventoryPage.setInventoryPageOrder(InventorySortOrderEnum.PRICE_HIGH_TO_LOW.value());
        var pricesList = inventoryPage.getAllPricesByOrder();
        var expectedPricesList = expectedProductList.stream()
                .sorted(Comparator.comparingDouble(Product::getPrice).reversed())
                .map(Product::getPrice)
                .collect(Collectors.toCollection(LinkedList::new));
        assertEquals(expectedPricesList, pricesList);
    }

    @TestTemplate
    @DisplayName("Inventory-page have correct cart items count")
    void inventoryPageHaveCorrectCartItemsCount() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        InventoryPage inventoryPage = new InventoryPage(page);
        for (Product product : expectedProductList) {
            inventoryPage.addProductToCart(product.getName());
        }
        assertEquals(expectedProductList.size(), inventoryPage.getCartItemCount());

        inventoryPage.removeProductFromCart(expectedProductList.getFirst().getName());
        assertEquals(expectedProductList.size() - 1, inventoryPage.getCartItemCount());
    }

    @TestTemplate
    @DisplayName("(Expected to fail and uncover a bug.) StandardUser. Product name shouldn't have reserved words")
    void inventoryPageNamesShouldNotHaveReservedWords() {

        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        InventoryPage inventoryPage = new InventoryPage(page);
        var productList = inventoryPage.getAllProductList();
        Optional<String> matchedWrong = productList.stream()
                .map(Product::getName)
                .filter(name -> RESERVED_WORDS.stream().anyMatch(name::contains))
                .findFirst();
        assertFalse(matchedWrong.isPresent(),
                () -> "Product name to contain a fragment from reserved words " + RESERVED_WORDS
                        + "; actual names: " + matchedWrong);
    }


    @TestTemplate
    @DisplayName("(Expected to fail and uncover a bug.) There is a code in the product description")
    void inventoryPageDescriptionShouldNotHaveReservedWords() {

        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        InventoryPage inventoryPage = new InventoryPage(page);
        var productList = inventoryPage.getAllProductList();
        Optional<String> matchedWrong = productList.stream()
                .map(Product::getDescription)
                .filter(desc -> desc != null && RESERVED_WORDS.stream().anyMatch(desc::contains))
                .findFirst();
        assertFalse(matchedWrong.isPresent(),
                () -> "Expected some product description to contain a fragment from reserved words "
                        + RESERVED_WORDS
                        + "; actual descriptions: " + matchedWrong);
    }

    @TestTemplate
    @DisplayName("Cart-page have correct item-list")
    void cartPageHaveCorrectItemList() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        InventoryPage inventoryPage = new InventoryPage(page);
        List<CartItem> expectedCartItemList = new ArrayList<>();
        int num = Math.min(expectedProductList.size(), 4);

        for (int i = 0; i < num; i++) {
            Product expected = expectedProductList.get(i);
            inventoryPage.addProductToCart(expected.getName());
            expectedCartItemList.add(
                    new CartItem()
                            .setQuantity(1)
                            .setName(expected.getName())
                            .setDescription(expected.getDescription())
                            .setPrice(expected.getPrice())
            );
        }
        CartPage cartPage = new CartPage(page);
        cartPage.navigateToCart();
        var cartItemList = cartPage.getAllCartItemList();
        assertEquals(expectedCartItemList, cartItemList);
    }

    @TestTemplate
    @DisplayName("Cart-page have correct total-price")
    void cartPageHaveCorrectTotalPrice() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        InventoryPage inventoryPage = new InventoryPage(page);
        for (Product product : expectedProductList) {
            inventoryPage.addProductToCart(product.getName());
        }

        BigDecimal expectedTotalPrice = expectedProductList.stream()
                .map(c -> BigDecimal.valueOf(c.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        CartPage cartPage = new CartPage(page);
        cartPage.navigateToCart();
        assertEquals(expectedTotalPrice, cartPage.getTotalPrice());
    }

    @TestTemplate
    @DisplayName("Cart-page have correct items-count")
    void cartPageHaveCorrectItemsCount() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);
        InventoryPage inventoryPage = new InventoryPage(page);
        for (Product product : expectedProductList) {
            inventoryPage.addProductToCart(product.getName());
        }

        CartPage cartPage = new CartPage(page);
        cartPage.navigateToCart();
        assertEquals(expectedProductList.size(), cartPage.getCartItemCount());

        cartPage.removeProductFromCart(expectedProductList.getFirst().getName());
        assertEquals(expectedProductList.size() - 1, cartPage.getCartItemCount());
    }

    @TestTemplate
    @DisplayName("checkout page have expected summery info")
    void checkoutProcessTest() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(USER_NAME, PASSWORD);

        assertTrue(page.url().contains("/inventory.html"),"Failed to log in");

        InventoryPage inventoryPage = new InventoryPage(page);
        CartPage cartPage = new CartPage(page);
        CheckoutPage checkoutPage;

        for (Product product : expectedProductList) {
            inventoryPage.addProductToCart(product.getName());
        }
        page.waitForCondition(
                () -> inventoryPage.getCartItemCount() == 6,
                new Page.WaitForConditionOptions().setTimeout(10000)
        );

        cartPage.navigateToCart();
        page.waitForURL("**/cart.html");
        cartPage.proceedToCheckout();
        checkoutPage = new CheckoutPage(page);
        checkoutPage.fillShippingInfo("John", "Doe", "12345");

        page.waitForURL("**/checkout-step-two.html");
        assertEquals(expectedSellSummeryInfo, new SellSummeryInfoPage(page).getSellSummeryInfo());

        checkoutPage.completePurchase();
        assertTrue(page.url().contains("/checkout-complete.html"));
    }

    @TestTemplate
    @DisplayName("Final checkout page have correct final message")
    void checkoutSuccessfulMessageTest() {

        new LoginPage(page).login(USER_NAME, PASSWORD);
        new InventoryPage(page).addProductToCart(expectedProductList.getFirst().getName());

        CartPage cartPage = new CartPage(page);
        cartPage.navigateToCart();
        cartPage.proceedToCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(page);
        checkoutPage.fillShippingInfo("John", "Doe", "12345");
        checkoutPage.completePurchase();

        assertAll(
                () -> assertTrue(checkoutPage.isOrderConfirmed()),
                () -> assertEquals("Thank you for your order!", checkoutPage.getConfirmationMessage())
        );
    }
}
