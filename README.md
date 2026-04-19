# Playwright — Sauce Demo (Swag Labs)

UI automation tests for the demo e-commerce site **[Sauce Demo](https://www.saucedemo.com/)** (Swag Labs). The application intentionally behaves differently per **test user**, which is useful for negative flows, timing, and data assertions.

## Six users:

| User | Typical site behavior (for test design) |
|------|----------------------------------------|
| `standard_user` | Happy-path shopping; product names/descriptions may contain demo artifacts such as `Test` or `()` — useful for content checks. |
| `locked_out_user` | Login is rejected — authentication failure scenarios. |
| `problem_user` | Display issues (e.g. product images) — UI assertions. |
| `performance_glitch_user` | Artificial delays on navigation (up to several seconds) — timeouts, waits, flaky-flow handling. |
| `error_user` | Some actions/buttons misbehave — negative and workaround scenarios. |
| `visual_user` | Suited for visual / screenshot-style scenarios (depending on test implementation). |

For accounts that are allowed to log in, the demo password is usually shared: `secret_sauce` (configured in this project via `application.properties`).

## Tech stack

- Java 21, Maven
- [Microsoft Playwright](https://playwright.dev/java/) (Java)
- JUnit 5
- Allure (reports)
- Owner (configuration from classpath properties)
- AssertJ

## Prerequisites

- JDK 21+
- Maven 3.6+

## Run tests

From the project root:

```bash
mvn test
```

## Allure report

After a test run that produces Allure results:

```bash
mvn allure:serve
```

## Configuration

Usernames and password are defined in `src/test/resources/application.properties` (keys such as `saucedemo.standard-username`, `saucedemo.standard-password`, etc.). The mapping is exposed through `com.saucedemo.config.SauceDemoTestConfig`.

## Project layout (overview)

| Path | Purpose |
|------|---------|
| `src/main/java/com/saucedemo/pages/` | Page Object classes |
| `src/test/java/com/saucedemo/tests/ui/` | UI tests per user / scenario |
| `src/test/java/com/saucedemo/tests/base/` | Base test setup, helpers (e.g. failure screenshots) |
| `src/test/java/com/saucedemo/config/` | Test configuration interfaces |
| `src/test/java/com/saucedemo/utils/` | CSV / data helpers |
| `src/test/resources/` | `application.properties`, static test data |

---

*Sauce Demo is a training site, not a real store.*
