package com.saucedemo.tests.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.*;
import com.saucedemo.models.Product;
import com.saucedemo.models.SellSummeryInfo;
import com.saucedemo.utils.ProductCsvReader;
import com.saucedemo.utils.SellSummeryInfoCsvReader;

import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URISyntaxException;
import java.util.List;

public class BaseTest {

    protected static ExtentReports extent;
    protected ExtentTest test;
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected static List<Product> expectedProductList;
    protected static SellSummeryInfo expectedSellSummeryInfo;

    @RegisterExtension
    ScreenshotWatcher screenshotWatcher = new ScreenshotWatcher();

    static {
        ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @BeforeAll
    static void setupStaticDataFromCsv() {
        try {
            var productListUrl = BaseTest.class.getClassLoader().getResource("static/test-data/productList.csv");
            if (productListUrl == null) {
                throw new IllegalStateException("Resource not found: static/test-data/productList.csv");
            }
            expectedProductList = ProductCsvReader.readProducts(Paths.get(productListUrl.toURI()).toString());

            var sellSummeryUrl = BaseTest.class.getClassLoader().getResource("static/test-data/sellSummeryInfo.csv");
            if (sellSummeryUrl == null) {
                throw new IllegalStateException("Resource not found: static/test-data/sellSummeryInfo.csv");
            }
            expectedSellSummeryInfo = SellSummeryInfoCsvReader.readProducts(Paths.get(sellSummeryUrl.toURI()).toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to resolve resource files for test-data CSV", e);
        }
    }

    @BeforeEach
    void setup() {
        test = extent.createTest(this.getClass().getSimpleName());
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void tearDown() {
        if (ScreenshotWatcher.getLastScreenshotPath() != null) {
            test.addScreenCaptureFromPath(ScreenshotWatcher.getLastScreenshotPath());
        }
        page.close();
        context.close();
        browser.close();
        playwright.close();
        extent.flush();
    }
}
