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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserTestInvocationContextProvider.class)
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

}
