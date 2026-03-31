package com.ibm.shopz.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.ibm.shopz.base.DriverFactory;
import com.ibm.shopz.config.ConfigReader;
import com.ibm.shopz.utils.ExtentReportManager;
import com.ibm.shopz.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNGListener - Implements ITestListener for TestNG event hooks.
 *
 * Integrates with ExtentReports to:
 *  - Log test start/pass/fail/skip events
 *  - Attach screenshots on failure (Base64 embedded)
 *  - Add error stack traces to the report
 *
 * Registered in testng.xml as a listener.
 */
public class TestNGListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestNGListener.class);
    private static final ConfigReader config = ConfigReader.getInstance();

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.info(">>> TEST STARTED: {}", testName);

        ExtentTest test = ExtentReportManager.getExtentTest();
        if (test != null) {
            test.info("Test started: " + testName);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.info("<<< TEST PASSED: {}", testName);

        ExtentTest test = ExtentReportManager.getExtentTest();
        if (test != null) {
            test.pass("Test passed successfully: " + testName);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.error("<<< TEST FAILED: {} - Cause: {}", testName,
                result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown");

        ExtentTest test = ExtentReportManager.getExtentTest();
        if (test != null) {
            test.fail("Test FAILED: " + testName);
            if (result.getThrowable() != null) {
                test.fail(result.getThrowable());
            }

            // Attach screenshot embedded as Base64
            if (config.isScreenshotOnFailure()) {
                try {
                    String base64Screenshot = ScreenshotUtils.captureScreenshotAsBase64(
                            DriverFactory.getDriver());
                    if (!base64Screenshot.isEmpty()) {
                        test.fail("Screenshot at failure:",
                                MediaEntityBuilder.createScreenCaptureFromBase64String(
                                        base64Screenshot, testName + "_failure").build());
                    }
                } catch (Exception e) {
                    logger.warn("Could not attach Base64 screenshot to report: {}", e.getMessage());
                    // Fallback: save to disk
                    String screenshotPath = ScreenshotUtils.captureScreenshot(
                            DriverFactory.getDriver(), testName);
                    try {
                        test.addScreenCaptureFromPath(screenshotPath);
                    } catch (Exception ex) {
                        logger.warn("Fallback screenshot attachment also failed: {}", ex.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.warn("<<< TEST SKIPPED: {}", testName);

        ExtentTest test = ExtentReportManager.getExtentTest();
        if (test != null) {
            test.skip("Test was skipped: " + testName);
            if (result.getThrowable() != null) {
                test.skip(result.getThrowable());
            }
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("Test failed but within success percentage: {}",
                result.getMethod().getMethodName());
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info("====== Test Suite Started: {} ======", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("====== Test Suite Finished: {} ======", context.getName());
        logger.info("Passed: {}, Failed: {}, Skipped: {}",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }
}
