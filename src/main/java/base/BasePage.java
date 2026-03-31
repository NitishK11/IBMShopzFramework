package com.ibm.shopz.base;

import com.ibm.shopz.config.ConfigReader;
import com.ibm.shopz.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * BasePage - Parent class for all Page Object classes.
 * Provides reusable Selenium 4 helper methods:
 *   - click, type, select dropdown, wait, scroll, etc.
 * All page classes extend this class.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;
    protected final ConfigReader config;
    private static final Logger logger = LogManager.getLogger(BasePage.class);

    /**
     * Constructor - initializes PageFactory, WebDriverWait, and Actions.
     *
     * @param driver WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigReader.getInstance();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWait()));
        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this);
    }

    // =========================================================
    //  Navigation Helpers
    // =========================================================

    /**
     * Navigate to the given URL.
     */
    public void navigateTo(String url) {
        logger.info("Navigating to URL: {}", url);
        driver.get(url);
    }

    /**
     * Get current page URL.
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Get current page title.
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    // =========================================================
    //  Wait Helpers
    // =========================================================

    public WebElement waitForVisibility(By locator) {
        logger.debug("Waiting for visibility of element: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForVisibility(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForClickability(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForClickability(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public boolean waitForInvisibility(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void waitForPageLoad() {
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));
        logger.debug("Page fully loaded.");
    }

    // =========================================================
    //  Interaction Helpers
    // =========================================================

    /**
     * Click on a WebElement.
     */
    public void click(WebElement element) {
        waitForClickability(element);
        logger.debug("Clicking element: {}", element);
        element.click();
    }

    /**
     * Click on element located by By locator.
     */
    public void click(By locator) {
        WebElement element = waitForClickability(locator);
        logger.debug("Clicking locator: {}", locator);
        element.click();
    }

    /**
     * Type text into a WebElement (clears first).
     */
    public void type(WebElement element, String text) {
        waitForVisibility(element);
        element.clear();
        element.sendKeys(text);
        logger.debug("Typed '{}' into element", text);
    }

    /**
     * Type text into element located by By locator.
     */
    public void type(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
        logger.debug("Typed '{}' into locator: {}", text, locator);
    }

    /**
     * Get text from a WebElement.
     */
    public String getText(WebElement element) {
        waitForVisibility(element);
        return element.getText().trim();
    }

    /**
     * Get text from element located by By locator.
     */
    public String getText(By locator) {
        return waitForVisibility(locator).getText().trim();
    }

    /**
     * Check if element is displayed.
     */
    public boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Check if element is displayed by locator.
     */
    public boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // =========================================================
    //  Dropdown (Select) Helpers
    // =========================================================

    /**
     * Select dropdown option by visible text.
     */
    public void selectByVisibleText(WebElement dropdownElement, String visibleText) {
        waitForVisibility(dropdownElement);
        Select select = new Select(dropdownElement);
        select.selectByVisibleText(visibleText);
        logger.info("Selected '{}' from dropdown", visibleText);
    }

    /**
     * Select dropdown option by value.
     */
    public void selectByValue(WebElement dropdownElement, String value) {
        waitForVisibility(dropdownElement);
        Select select = new Select(dropdownElement);
        select.selectByValue(value);
        logger.info("Selected value '{}' from dropdown", value);
    }

    /**
     * Select dropdown option by index.
     */
    public void selectByIndex(WebElement dropdownElement, int index) {
        waitForVisibility(dropdownElement);
        Select select = new Select(dropdownElement);
        select.selectByIndex(index);
        logger.info("Selected index {} from dropdown", index);
    }

    /**
     * Get the currently selected dropdown text.
     */
    public String getSelectedDropdownText(WebElement dropdownElement) {
        Select select = new Select(dropdownElement);
        return select.getFirstSelectedOption().getText().trim();
    }

    /**
     * Get all options from a dropdown as a list of WebElements.
     */
    public List<WebElement> getAllDropdownOptions(WebElement dropdownElement) {
        Select select = new Select(dropdownElement);
        return select.getOptions();
    }

    // =========================================================
    //  Scroll Helpers
    // =========================================================

    /**
     * Scroll to a WebElement using JavaScript.
     */
    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        logger.debug("Scrolled to element: {}", element);
    }

    /**
     * Scroll to top of the page.
     */
    public void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
    }

    /**
     * Scroll to bottom of the page.
     */
    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    // =========================================================
    //  JavaScript Helpers
    // =========================================================

    /**
     * Click element via JavaScript (fallback for non-interactable elements).
     */
    public void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        logger.debug("JS-clicked element: {}", element);
    }

    /**
     * Highlight a WebElement (useful during debugging).
     */
    public void highlightElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red'", element);
    }

    // =========================================================
    //  Screenshot Helper
    // =========================================================

    /**
     * Take a screenshot and return the file path.
     */
    public String takeScreenshot(String testName) {
        return ScreenshotUtils.captureScreenshot(driver, testName);
    }

    // =========================================================
    //  Alert Helpers
    // =========================================================

    public void acceptAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).accept();
    }

    public void dismissAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).dismiss();
    }

    public String getAlertText() {
        return wait.until(ExpectedConditions.alertIsPresent()).getText();
    }

    // =========================================================
    //  Window / Tab Helpers
    // =========================================================

    /**
     * Switch to a new tab/window.
     */
    public void switchToNewWindow() {
        String currentWindow = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(currentWindow)) {
                driver.switchTo().window(handle);
                logger.info("Switched to new window: {}", handle);
                break;
            }
        }
    }

    /**
     * Close current window and switch back to original.
     */
    public void closeCurrentWindowAndSwitch(String originalHandle) {
        driver.close();
        driver.switchTo().window(originalHandle);
    }

    /**
     * Get the main window handle.
     */
    public String getMainWindowHandle() {
        return driver.getWindowHandle();
    }
}
