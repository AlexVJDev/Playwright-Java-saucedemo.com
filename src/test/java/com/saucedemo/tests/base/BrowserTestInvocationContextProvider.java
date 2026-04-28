package com.saucedemo.tests.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

/**
 * Runs each {@link org.junit.jupiter.api.TestTemplate} against Chromium, Firefox, and WebKit.
 */
public class BrowserTestInvocationContextProvider implements TestTemplateInvocationContextProvider {

    private static BrowserType.LaunchOptions launchOpts() {
        return new BrowserType.LaunchOptions().setHeadless(false);
    }

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        return Stream.of(
                invocation("chromium", Playwright::chromium),
                invocation("firefox", Playwright::firefox),
                invocation("webkit", Playwright::webkit)
        );
    }

    private TestTemplateInvocationContext invocation(
            String label, Function<Playwright, BrowserType> browserTypeFromPlaywright) {

        Function<Playwright, Browser> launcher =
                pw -> browserTypeFromPlaywright.apply(pw).launch(launchOpts());

        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return "[" + label + "]";
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return Collections.singletonList(new BrowserSessionExtension(launcher));
            }
        };
    }

    private static final class BrowserSessionExtension implements BeforeEachCallback, AfterEachCallback {

        private final Function<Playwright, Browser> launcher;

        private BrowserSessionExtension(Function<Playwright, Browser> launcher) {
            this.launcher = launcher;
        }

        @Override
        public void beforeEach(ExtensionContext extensionContext) {
            BaseTest base = (BaseTest) extensionContext.getRequiredTestInstance();
            base.test = BaseTest.extent.createTest(extensionContext.getDisplayName());
            base.playwright = Playwright.create();
            base.browser = launcher.apply(base.playwright);
            base.context = base.browser.newContext();
            base.page = base.context.newPage();
            ScreenshotWatcher.setCurrentPage(base.page);
        }

        @Override
        public void afterEach(ExtensionContext extensionContext) {
            BaseTest base = (BaseTest) extensionContext.getRequiredTestInstance();
            if (ScreenshotWatcher.getLastScreenshotPath() != null) {
                base.test.addScreenCaptureFromPath(ScreenshotWatcher.getLastScreenshotPath());
            }
            if (base.page != null) {
                base.page.close();
            }
            if (base.context != null) {
                base.context.close();
            }
            if (base.browser != null) {
                base.browser.close();
            }
            if (base.playwright != null) {
                base.playwright.close();
            }
            BaseTest.extent.flush();
            ScreenshotWatcher.clearCurrentPage();
        }
    }
}
