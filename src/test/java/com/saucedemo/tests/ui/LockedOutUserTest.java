package com.saucedemo.tests.ui;

import com.saucedemo.config.SauceDemoTestConfig;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.tests.base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LockedOutUserTest extends BaseTest {

    private static final SauceDemoTestConfig CFG = SauceDemoTestConfig.get();
    private static final String USER_NAME = CFG.lockedOutUser();
    private static final String PASSWORD = CFG.standardPassword();

    @Test
    @DisplayName("Authorization does not work for lockedOutUser")
    void loginFailedUserLocked() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.submitLoginForm(USER_NAME, PASSWORD);
        assertFalse(page.url().contains("/inventory.html"), "Must stay not logged when USER_NAME lockedOutUser");
        assertEquals("Epic sadface: Sorry, this user has been locked out.", loginPage.getErrorMessage());
    }
}
