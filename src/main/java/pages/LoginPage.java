package com.ibm.shopz.pages;

import com.ibm.shopz.base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * LoginPage - Page Object for:
 * https://www.ibm.com/software/shopzseries/ShopzSeries.wss?action=login
 *
 * IBM ShopZ Sign In / Register page.
 * Users authenticate with IBM ID before accessing orders, downloads, etc.
 */
public class LoginPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(LoginPage.class);

    public static final String PAGE_URL =
            "https://www.ibm.com/software/shopzseries/ShopzSeries.wss?action=login";

    // =========================================================
    //  Page Elements
    // =========================================================

    /** IBM ID (username) input field */
    @FindBy(id = "username")
    private WebElement usernameField;

    /** Password input field */
    @FindBy(id = "password")
    private WebElement passwordField;

    /** Sign In submit button */
    @FindBy(css = "button[type='submit'], input[type='submit']")
    private WebElement signInButton;

    /** "Sign in with my IBM ID" link */
    @FindBy(linkText = "Sign in with my IBM ID")
    private WebElement signInWithIBMIDLink;

    /** Register link (new user) */
    @FindBy(xpath = "//a[contains(text(),'Register') or contains(text(),'register')]")
    private WebElement registerLink;

    /** Error message element */
    @FindBy(css = ".error-message, .alert-danger, #error-msg")
    private WebElement errorMessage;

    /** Page heading */
    @FindBy(css = "h1, h2, .login-title")
    private WebElement pageHeading;

    // =========================================================
    //  Constructor
    // =========================================================

    public LoginPage(WebDriver driver) {
        super(driver);
        logger.info("LoginPage initialized");
    }

    // =========================================================
    //  Navigation
    // =========================================================

    /**
     * Navigate to Login page.
     */
    public LoginPage open() {
        navigateTo(PAGE_URL);
        waitForPageLoad();
        logger.info("Opened Login page: {}", PAGE_URL);
        return this;
    }

    // =========================================================
    //  Actions
    // =========================================================

    /**
     * Enter IBM ID (username).
     */
    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }

    /**
     * Enter password.
     */
    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    /**
     * Click the Sign In button.
     */
    public void clickSignIn() {
        click(signInButton);
        waitForPageLoad();
    }

    /**
     * Full login action.
     *
     * @param username IBM ID
     * @param password password
     */
    public void login(String username, String password) {
        logger.info("Attempting login with username: {}", username);
        enterUsername(username);
        enterPassword(password);
        clickSignIn();
    }

    // =========================================================
    //  Validations
    // =========================================================

    /**
     * Check if the login page is displayed correctly.
     */
    public boolean isLoginPageDisplayed() {
        return isDisplayed(usernameField) || isDisplayed(signInWithIBMIDLink);
    }

    /**
     * Check if an error message is displayed.
     */
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }

    /**
     * Get error message text.
     */
    public String getErrorMessageText() {
        return getText(errorMessage);
    }

    /**
     * Get page heading text.
     */
    public String getPageHeadingText() {
        try {
            return getText(pageHeading);
        } catch (Exception e) {
            return getPageTitle();
        }
    }
}
