package com.ibm.shopz.utils;

import com.ibm.shopz.config.ConfigReader;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtils - Utility class for capturing screenshots.
 *
 * Used in:
 *  - BaseTest.tearDown() on test failure
 *  - Manually from any test/page for debugging
 *  - ExtentReport attachment on failure
 */
public class ScreenshotUtils {

    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final ConfigReader config = ConfigReader.getInstance();

    private ScreenshotUtils() {
        // Utility class
    }

    /**
     * Capture a screenshot and save to the configured screenshots directory.
     *
     * @param driver   WebDriver instance
     * @param testName test name (used in the filename)
     * @return absolute file path of saved screenshot, or empty string on failure
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String safeTestName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = safeTestName + "_" + timestamp + ".png";
            String screenshotDir = config.getScreenshotPath();

            // Ensure directory exists
            File dir = new File(screenshotDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File destFile = new File(screenshotDir + fileName);
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, destFile);

            String absolutePath = destFile.getAbsolutePath();
            logger.info("Screenshot saved: {}", absolutePath);
            return absolutePath;

        } catch (Exception e) {
            logger.error("Failed to capture screenshot for test '{}': {}", testName, e.getMessage());
            return "";
        }
    }

    /**
     * Capture a screenshot as a Base64 string (useful for embedding in reports).
     *
     * @param driver WebDriver instance
     * @return Base64 encoded screenshot string
     */
    public static String captureScreenshotAsBase64(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as Base64: {}", e.getMessage());
            return "";
        }
    }
}
