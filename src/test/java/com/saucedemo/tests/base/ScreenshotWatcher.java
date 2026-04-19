package com.saucedemo.tests.base;

import com.microsoft.playwright.Page;
import lombok.Getter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScreenshotWatcher implements TestWatcher {

    @Getter
    private static String lastScreenshotPath;

    private static final ThreadLocal<Page> pageHolder = new ThreadLocal<>();

    public static void setCurrentPage(Page page) {
        pageHolder.set(page);
    }

    public static void clearCurrentPage() {
        pageHolder.remove();
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Page page = pageHolder.get();
        if (page == null) {
            return;
        }

        String testName = context.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_");
        Path path = Paths.get("target/screenshots", testName + "_FAILED.png");
        try {
            page.screenshot(new Page.ScreenshotOptions().setPath(path));
            lastScreenshotPath = path.toString();
        } catch (Exception e) {
            System.err.println("Error while creating a screenshot: " + e.getMessage());
        }
    }
}

