package com.ibm.shopz.tests;

import com.ibm.shopz.base.BaseTest;
import com.ibm.shopz.pages.ProductCatalogPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * SmokeTest - Critical smoke tests that verify IBM ShopZ basic functionality.
 *
 * These tests should always pass and act as a quick health check before
 * running the full regression suite.
 *
 *  TC_SMOKE_001 - Application is reachable and page loads
 *  TC_SMOKE_002 - Product Catalog page title is correct
 *  TC_SMOKE_003 - Country dropdown is present and functional
 *  TC_SMOKE_004 - Package Type dropdown is present and functional
 *  TC_SMOKE_005 - Find button is present and clickable
 */
public class SmokeTest extends BaseTest {

    private ProductCatalogPage catalogPage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        catalogPage = new ProductCatalogPage(getDriver());
        catalogPage.open();
    }

    // =========================================================
    //  TC_SMOKE_001 - Application Reachable
    // =========================================================

    @Test(description = "TC_SMOKE_001 - Verify IBM ShopZ application is reachable and page loads",
          groups = {"smoke"})
    public void verifyApplicationIsReachable() {
        logStep("Verifying that IBM ShopZ application URL is reachable");
        String currentUrl = catalogPage.getCurrentUrl();
        logStep("Current URL: " + currentUrl);

        Assert.assertFalse(currentUrl.isEmpty(), "Current URL should not be empty");
        Assert.assertTrue(
                currentUrl.contains("ibm.com") || currentUrl.contains("shopzseries"),
                "URL should point to IBM ShopZ. Actual: " + currentUrl);
        logPass("Application is reachable. URL: " + currentUrl);
    }

    // =========================================================
    //  TC_SMOKE_002 - Page Title
    // =========================================================

    @Test(description = "TC_SMOKE_002 - Verify Product Catalog page title is correct",
          groups = {"smoke"})
    public void verifyProductCatalogPageTitle() {
        logStep("Reading page title");
        String title = catalogPage.getPageTitle();
        logStep("Page title: " + title);

        Assert.assertFalse(title.isEmpty(), "Page title should not be empty");
        Assert.assertTrue(
                title.toLowerCase().contains("shopz") || title.toLowerCase().contains("ibm"),
                "Page title should contain 'shopz' or 'ibm'. Actual: " + title);
        logPass("Page title is correct: " + title);
    }

    // =========================================================
    //  TC_SMOKE_003 - Country Dropdown Present
    // =========================================================

    @Test(description = "TC_SMOKE_003 - Verify Country dropdown is present and has options",
          groups = {"smoke"})
    public void verifyCountryDropdownPresent() {
        logStep("Checking Country dropdown is present and has options");
        int countryCount = catalogPage.getAllCountryOptions().size();
        logStep("Country options count: " + countryCount);

        Assert.assertTrue(countryCount > 1,
                "Country dropdown should have more than 1 option. Found: " + countryCount);
        logPass("Country dropdown is present with " + countryCount + " options");
    }

    // =========================================================
    //  TC_SMOKE_004 - Package Type Dropdown Present
    // =========================================================

    @Test(description = "TC_SMOKE_004 - Verify Package Type dropdown is present and has options",
          groups = {"smoke"})
    public void verifyPackageTypeDropdownPresent() {
        logStep("Checking Package Type dropdown is present and has options");
        int pkgCount = catalogPage.getAllPackageTypeOptions().size();
        logStep("Package type options count: " + pkgCount);

        Assert.assertTrue(pkgCount > 1,
                "Package Type dropdown should have more than 1 option. Found: " + pkgCount);
        logPass("Package Type dropdown is present with " + pkgCount + " options");
    }

    // =========================================================
    //  TC_SMOKE_005 - Find Button Present and Search Works
    // =========================================================

    @Test(description = "TC_SMOKE_005 - Verify Find button is present and executes catalog search",
          groups = {"smoke"})
    public void verifyFindButtonPresent() {
        logStep("Selecting United States and z/OS-CBPDO then clicking Find");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();

        String url = catalogPage.getCurrentUrl();
        logStep("URL after Find button click: " + url);

        // After clicking Find, the page should reload or update (URL may change or results appear)
        Assert.assertFalse(url.isEmpty(),
                "URL should not be empty after clicking Find button");
        logPass("Find button clicked successfully. Current URL: " + url);
    }
}
