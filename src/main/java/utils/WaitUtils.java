package com.ibm.shopz.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitUtils - Standalone wait utility methods.
 * Supplements BasePage's built-in waits for use in
 * places where a BasePage instance is not available.
 */
public class WaitUtils {

    private static final Logger logger = LogManager.getLogger(WaitUtils.class);

    private WaitUtils() {}

    /**
     * Wait for element to be visible.
     */
    public static WebElement waitForVisibility(WebDriver driver, By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for element to be clickable.
     */
    public static WebElement waitForClickability(WebDriver driver, By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait for text to be present in element.
     */
    public static boolean waitForTextPresent(WebDriver driver, By locator,
                                              String text, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * Hard sleep (use sparingly - prefer explicit waits).
     */
    public static void hardWait(int milliseconds) {
        try {
            logger.debug("Hard wait: {}ms", milliseconds);
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Hard wait interrupted: {}", e.getMessage());
        }
    }

    /**
     * Wait for page URL to contain a specific text.
     */
    public static boolean waitForUrlContains(WebDriver driver, String urlFragment, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Wait for page title to contain specific text.
     */
    public static boolean waitForTitleContains(WebDriver driver, String title, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.titleContains(title));
    }
}
