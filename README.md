# IBM ShopZ Selenium 4 Automation Framework

A complete **Page Object Model (POM)** test automation framework built for
[IBM ShopZ Product Catalog](https://www.ibm.com/software/shopzseries/ShopzSeries_public.wss?action=prodcat)
using **Selenium 4**, **TestNG**, and **ExtentReports 5**.

---

## Framework Architecture

```
IBMShopzFramework/
│
├── pom.xml                                          ← Maven dependencies & build config
│
├── src/
│   ├── main/java/com/ibm/shopz/
│   │   ├── base/
│   │   │   ├── BasePage.java                       ← Parent Page Object (Selenium helpers)
│   │   │   ├── BaseTest.java                       ← Parent Test Class (setup/teardown)
│   │   │   └── DriverFactory.java                  ← ThreadLocal WebDriver factory
│   │   │
│   │   ├── config/
│   │   │   └── ConfigReader.java                   ← Singleton config.properties reader
│   │   │
│   │   ├── pages/
│   │   │   ├── ProductCatalogPage.java             ← POM: Main catalog page
│   │   │   ├── FmidDetailPage.java                 ← POM: FMID detail page
│   │   │   ├── ProductDetailPage.java              ← POM: Product detail page (5655-GOZ)
│   │   │   └── LoginPage.java                      ← POM: Sign In page
│   │   │
│   │   ├── utils/
│   │   │   ├── ExtentReportManager.java            ← ExtentReports 5 manager
│   │   │   ├── ScreenshotUtils.java                ← Screenshot capture utility
│   │   │   └── WaitUtils.java                      ← Standalone wait helpers
│   │   │
│   │   └── listeners/
│   │       └── TestNGListener.java                 ← ITestListener (report + screenshot hooks)
│   │
│   └── test/
│       ├── java/com/ibm/shopz/tests/
│       │   ├── SmokeTest.java                      ← Smoke: 5 critical health checks
│       │   ├── ProductCatalogPageTest.java          ← 15 catalog page tests (TC_001–TC_015)
│       │   ├── FmidDetailPageTest.java             ← 7 FMID navigation tests
│       │   ├── ProductDetailPageTest.java          ← 6 product detail + E2E tests
│       │   └── NavigationTest.java                 ← 7 navigation/link tests
│       │
│       └── resources/
│           ├── config.properties                   ← All configurable parameters
│           ├── log4j2.xml                          ← Log4j2 console + file logging config
│           ├── testng.xml                          ← Full regression suite (parallel)
│           ├── testng-smoke.xml                    ← Smoke-only suite
│           └── testng-e2e.xml                      ← E2E-only suite
│
├── reports/                                        ← ExtentReports HTML output (auto-created)
├── screenshots/                                    ← Failure screenshots (auto-created)
└── logs/                                           ← Log4j2 rolling logs (auto-created)
```

---

## Prerequisites

| Tool            | Version    | Notes                              |
|-----------------|------------|------------------------------------|
| Java            | 11+        | JDK required                       |
| Maven           | 3.8+       | For build and test execution       |
| Chrome          | Latest     | Default browser                    |
| Firefox/Edge    | Latest     | Optional - configurable            |
| ChromeDriver    | Auto       | Managed by WebDriverManager        |

---

## Quick Setup

### 1. Clone / extract the project

```bash
cd IBMShopzFramework
```

### 2. Install dependencies

```bash
mvn clean install -DskipTests
```

### 3. Run smoke tests

```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-smoke.xml
```

### 4. Run full regression suite

```bash
mvn test
```

### 5. Run on a different browser

```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
```

### 6. Run headless

Set in `config.properties`:
```properties
headless=true
```
Or override:
```bash
mvn test -Dheadless=true
```

### 7. Run specific test groups

```bash
# Smoke only
mvn test -Dgroups=smoke

# Regression only
mvn test -Dgroups=regression

# E2E only
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-e2e.xml
```

---

## Configuration Reference (`config.properties`)

| Property                    | Default                        | Description                        |
|-----------------------------|--------------------------------|------------------------------------|
| `base.url`                  | IBM ShopZ catalog URL          | Application entry URL              |
| `browser`                   | `chrome`                       | `chrome` / `firefox` / `edge`      |
| `headless`                  | `false`                        | Run in headless mode               |
| `implicit.wait`             | `10`                           | Seconds for implicit wait          |
| `explicit.wait`             | `20`                           | Seconds for explicit wait          |
| `page.load.timeout`         | `30`                           | Seconds for page load timeout      |
| `screenshot.on.failure`     | `true`                         | Auto-screenshot on test failure    |
| `screenshot.path`           | `screenshots/`                 | Directory for failure screenshots  |
| `report.path`               | `reports/`                     | Directory for ExtentReport HTML    |
| `report.name`               | `IBMShopzTestReport`           | Report file name prefix            |
| `report.title`              | `IBM ShopZ Automation Report`  | Title shown in HTML report         |

---

## Test Coverage Summary

| Test Class               | Count | Groups                        |
|--------------------------|-------|-------------------------------|
| `SmokeTest`              | 5     | smoke                         |
| `ProductCatalogPageTest` | 15    | smoke, regression, functional |
| `FmidDetailPageTest`     | 7     | regression, functional, navigation |
| `ProductDetailPageTest`  | 6     | regression, functional, e2e   |
| `NavigationTest`         | 7     | smoke, regression, navigation |
| **Total**                | **40**|                               |

---

## Test Cases: ProductCatalogPage

| Test ID   | Description                                                     |
|-----------|-----------------------------------------------------------------|
| TC_001    | Verify page loads with correct title                            |
| TC_002    | Verify all 4 filter dropdowns are displayed                     |
| TC_003    | Verify NOTE message about latest version is shown               |
| TC_004    | Verify Country dropdown has multiple options incl. "United States" |
| TC_005    | Verify all 6 Package Type options are present (from doc)        |
| TC_006    | Verify Group dropdown is displayed and has options              |
| TC_007    | Verify Language dropdown has options                            |
| TC_008    | Select "United States" and verify selection                     |
| TC_009    | Search with z/OS-CBPDO package type                             |
| TC_010    | Search with Linux on z-Standalone package type                  |
| TC_011    | Search with z/VM-VM SDO version 7                               |
| TC_012    | Search with z/VSE-VSE SIPO version 6                            |
| TC_013    | Verify product count label after search                         |
| TC_014    | Verify results table appears after valid search                 |
| TC_015    | Verify FMID links are present after valid search                |

---

## Reports

After execution, HTML reports are generated in the `reports/` directory:
```
reports/IBMShopzTestReport_2024-01-15_10-30-00.html
```

Open in any browser. The report includes:
- Test pass/fail/skip summary with counts
- Per-test step-by-step logs
- Embedded Base64 screenshots for all failures
- System info (OS, browser, Java version, base URL)
- Test category tags (smoke / regression / e2e)

---

## Logging

Log4j2 logs are written to:
- **Console** - real-time output during execution
- **`logs/automation.log`** - rolling file log (10MB max, 10 files retained)

---

## Framework Key Design Decisions

### Thread-Safety
- `DriverFactory` uses `ThreadLocal<WebDriver>` — safe for parallel test execution.
- `ExtentReportManager` uses `ThreadLocal<ExtentTest>` — each thread gets its own test node.

### Page Object Model
- All pages extend `BasePage` which provides reusable Selenium 4 helpers.
- `PageFactory.initElements()` is called in `BasePage` constructor for `@FindBy` injection.
- Pages return `this` or a new page object for fluent chaining: `catalogPage.selectCountry("United States").selectPackageType("...").clickFind()`.

### Report Integration
- `TestNGListener` hooks into TestNG events (`onTestSuccess`, `onTestFailure`, `onTestSkipped`).
- Failures automatically embed Base64 screenshots directly in the ExtentReport HTML.

### Configuration
- All settings are externalized in `config.properties` - no hardcoded values in tests.
- `ConfigReader` is a singleton to avoid repeated file I/O.

---

## Extending the Framework

### Add a new Page Object
```java
public class MyNewPage extends BasePage {
    @FindBy(id = "myElement")
    private WebElement myElement;

    public MyNewPage(WebDriver driver) {
        super(driver);  // PageFactory initialized in BasePage
    }

    public void doSomething() {
        click(myElement);
    }
}
```

### Add a new Test Class
```java
public class MyNewTest extends BaseTest {
    @Test(description = "My test", groups = {"regression"})
    public void myTest() {
        logStep("Step 1 - Open page");
        MyNewPage page = new MyNewPage(getDriver());
        // assertions...
        logPass("Test passed");
    }
}
```

### Add test to testng.xml
```xml
<class name="com.ibm.shopz.tests.MyNewTest"/>
```
