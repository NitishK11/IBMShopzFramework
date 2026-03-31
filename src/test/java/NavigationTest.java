package com.ibm.shopz.tests;

import com.ibm.shopz.base.BaseTest;
import com.ibm.shopz.pages.LoginPage;
import com.ibm.shopz.pages.ProductCatalogPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * NavigationTest - TestNG tests for top navigation links on IBM ShopZ.
 *
 * Test Coverage:
 *  TC_NAV_001 - Verify Sign In/Register link opens login page
 *  TC_NAV_002 - Verify FAQs navigation link works
 *  TC_NAV_003 - Verify Glossary navigation link works
 *  TC_NAV_004 - Verify Users' Guide navigation link works
 *  TC_NAV_005 - Verify Customer Service navigation link works
 *  TC_NAV_006 - Verify Login page URL is correct
 *  TC_NAV_007 - Verify Login page is displayed with required elements
 */
public class NavigationTest extends BaseTest {

    private ProductCatalogPage catalogPage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        catalogPage = new ProductCatalogPage(getDriver());
        catalogPage.open();
    }

    // =========================================================
    //  TC_NAV_001 - Sign In / Register Link
    // =========================================================

    @Test(description = "TC_NAV_001 - Verify Sign In/Register link navigates to login page",
          groups = {"smoke", "regression", "navigation"})
    public void verifySignInRegisterLink() {
        logStep("Clicking Sign In / Register link");
        catalogPage.clickSignInRegister();

        String currentUrl = getDriver().getCurrentUrl();
        logStep("URL after clicking Sign In/Register: " + currentUrl);

        Assert.assertTrue(
                currentUrl.contains("login") || currentUrl.contains("signin")
                        || currentUrl.contains("ShopzSeries.wss"),
                "URL should point to login page after clicking Sign In. Actual: " + currentUrl);
        logPass("Sign In/Register navigated to: " + currentUrl);
    }

    // =========================================================
    //  TC_NAV_002 - FAQs Link
    // =========================================================

    @Test(description = "TC_NAV_002 - Verify FAQs link navigates to FAQ page",
          groups = {"regression", "navigation"})
    public void verifyFaqsLink() {
        logStep("Clicking FAQs link in navigation");
        catalogPage.clickFAQs();

        String currentUrl = getDriver().getCurrentUrl();
        logStep("URL after clicking FAQs: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("faq"),
                "URL should contain 'faq' after clicking FAQs link. Actual: " + currentUrl);
        logPass("FAQs link navigated correctly to: " + currentUrl);
    }

    // =========================================================
    //  TC_NAV_003 - Glossary Link
    // =========================================================

    @Test(description = "TC_NAV_003 - Verify Glossary link navigates to Glossary page",
          groups = {"regression", "navigation"})
    public void verifyGlossaryLink() {
        logStep("Clicking Glossary link in navigation");
        catalogPage.clickGlossary();

        String currentUrl = getDriver().getCurrentUrl();
        logStep("URL after clicking Glossary: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("glossary"),
                "URL should contain 'glossary' after clicking Glossary link. Actual: " + currentUrl);
        logPass("Glossary link navigated correctly to: " + currentUrl);
    }

    // =========================================================
    //  TC_NAV_004 - Users' Guide Link
    // =========================================================

    @Test(description = "TC_NAV_004 - Verify Users' Guide link navigates to guide page",
          groups = {"regression", "navigation"})
    public void verifyUsersGuideLink() {
        logStep("Clicking Users' Guide link in navigation");
        catalogPage.clickUsersGuide();

        String currentUrl = getDriver().getCurrentUrl();
        logStep("URL after clicking Users Guide: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("guide"),
                "URL should contain 'guide' after clicking Users Guide link. Actual: " + currentUrl);
        logPass("Users' Guide link navigated correctly to: " + currentUrl);
    }

    // =========================================================
    //  TC_NAV_005 - Customer Service Link
    // =========================================================

    @Test(description = "TC_NAV_005 - Verify Customer Service link navigates to service page",
          groups = {"regression", "navigation"})
    public void verifyCustomerServiceLink() {
        logStep("Clicking Customer Service link in navigation");
        catalogPage.clickCustomerService();

        String currentUrl = getDriver().getCurrentUrl();
        logStep("URL after clicking Customer Service: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("service"),
                "URL should contain 'service' after clicking Customer Service. Actual: " + currentUrl);
        logPass("Customer Service link navigated correctly to: " + currentUrl);
    }

    // =========================================================
    //  TC_NAV_006 - Login Page URL
    // =========================================================

    @Test(description = "TC_NAV_006 - Verify Login page URL is correct when opened directly",
          groups = {"smoke", "regression"})
    public void verifyLoginPageUrl() {
        logStep("Opening Login page directly via URL");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();

        String loginUrl = getDriver().getCurrentUrl();
        logStep("Login page URL: " + loginUrl);

        Assert.assertTrue(
                loginUrl.contains("login") || loginUrl.contains("ShopzSeries.wss"),
                "Login page URL should contain 'login' or 'ShopzSeries.wss'. Actual: " + loginUrl);
        logPass("Login page URL verified: " + loginUrl);
    }

    // =========================================================
    //  TC_NAV_007 - Login Page Elements Displayed
    // =========================================================

    @Test(description = "TC_NAV_007 - Verify Login page displays required elements",
          groups = {"smoke", "regression"})
    public void verifyLoginPageElements() {
        logStep("Opening Login page directly");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();

        boolean loginPageDisplayed = loginPage.isLoginPageDisplayed();
        logStep("Login page displayed: " + loginPageDisplayed);
        Assert.assertTrue(loginPageDisplayed,
                "Login page should display IBM ID sign-in elements");
        logPass("Login page elements are displayed correctly");
    }
}
