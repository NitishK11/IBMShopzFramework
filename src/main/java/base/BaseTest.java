package com.ibm.shopz.base;

import com.ibm.shopz.config.ConfigReader;
import com.ibm.shopz.utils.ExtentReportManager;
import com.ibm.shopz.utils.ScreenshotUtils;
import com.aventstack.extentreports.ExtentTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * BaseTest - Parent class for all TestNG test classes.
 * Responsibilities:
 *  - Initialize/quit WebDriver via @BeforeMethod / @AfterMethod
 *  - Manage ExtentTest lifecycle per test method
 *  - Capture screenshot on failure
 *  - Flush ExtentReports after each class / full suite
 */
public class BaseTest {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected ConfigReader config = ConfigReader.getInstance();

    // Per-thread ExtentTest (for parallel safety)
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    // =========================================================
    //  Suite-level setup / teardown
    // =========================================================

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.info("====== IBM ShopZ Test Suite Starting ======");
        ExtentReportManager.initReport();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentReportManager.flushReport();
        logger.info("====== IBM ShopZ Test Suite Completed ======");
    }

    // =========================================================
    //  Method-level setup / teardown
    // =========================================================

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional("chrome") String browser, ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String testDescription = result.getMethod().getDescription();
        String groups = String.join(", ", result.getMethod().getGroups());

        logger.info("--- Starting Test: {} ---", testName);

        // Initialize WebDriver
        DriverFactory.initDriver(browser);

        // Create ExtentTest node
        ExtentTest extentTest = ExtentReportManager.createTest(testName, testDescription);
        if (!groups.isEmpty()) {
            extentTest.assignCategory(groups);
        }
        extentTestThreadLocal.set(extentTest);

        // Navigate to base URL
        DriverFactory.getDriver().get(config.getBaseUrl());
        logger.info("Navigated to: {}", config.getBaseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest extentTest = extentTestThreadLocal.get();

        if (result.getStatus() == ITestResult.SUCCESS) {
            logger.info("Test PASSED: {}", testName);
            if (extentTest != null) {
                extentTest.pass("Test Passed: " + testName);
            }
        } else if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("Test FAILED: {} - {}", testName, result.getThrowable());
            if (extentTest != null) {
                extentTest.fail(result.getThrowable());
                // Capture and embed screenshot
                if (config.isScreenshotOnFailure()) {
                    String screenshotPath = ScreenshotUtils.captureScreenshot(
                            DriverFactory.getDriver(), testName);
                    try {
                        extentTest.addScreenCaptureFromPath(screenshotPath,
                                "Screenshot on Failure: " + testName);
                    } catch (Exception e) {
                        logger.warn("Could not attach screenshot to report: {}", e.getMessage());
                    }
                }
            }
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.warn("Test SKIPPED: {}", testName);
            if (extentTest != null) {
                extentTest.skip("Test Skipped: " + testName);
            }
        }

        // Quit driver
        DriverFactory.quitDriver();
        extentTestThreadLocal.remove();
    }

    // =========================================================
    //  Helper Accessors for subclasses
    // =========================================================

    /**
     * Returns the WebDriver for the current thread.
     */
    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    /**
     * Returns the ExtentTest for the current thread.
     */
    protected ExtentTest getExtentTest() {
        return extentTestThreadLocal.get();
    }

    /**
     * Log info step to both Log4j2 and ExtentReport.
     */
    protected void logStep(String message) {
        logger.info(message);
        ExtentTest test = extentTestThreadLocal.get();
        if (test != null) {
            test.info(message);
        }
    }

    /**
     * Log pass step to ExtentReport.
     */
    protected void logPass(String message) {
        logger.info("PASS: {}", message);
        ExtentTest test = extentTestThreadLocal.get();
        if (test != null) {
            test.pass(message);
        }
    }

    /**
     * Log fail step to ExtentReport.
     */
    protected void logFail(String message) {
        logger.error("FAIL: {}", message);
        ExtentTest test = extentTestThreadLocal.get();
        if (test != null) {
            test.fail(message);
        }
    }
}
