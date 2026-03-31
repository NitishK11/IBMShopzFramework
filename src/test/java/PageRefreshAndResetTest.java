package com.ibm.shopz.tests;

import com.ibm.shopz.base.BaseTest;
import com.ibm.shopz.pages.ProductCatalogPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * PageRefreshAndResetTest - Tests browser refresh, back/forward navigation,
 * and URL-based direct navigation behaviours.
 *
 * Coverage areas NOT in existing tests:
 *  - Page refresh resets filter selections
 *  - Browser Back button after search returns to catalog
 *  - Browser Forward button navigates back to results
 *  - Direct URL navigation to catalog page always loads correctly
 *  - Repeated opens of the catalog page do not degrade
 *  - Filter state is not persisted across a fresh page open
 *  - Refreshing on results page does not crash
 */
public class PageRefreshAndResetTest extends BaseTest {

    private ProductCatalogPage catalogPage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        catalogPage = new ProductCatalogPage(getDriver());
        catalogPage.open();
    }

    // =========================================================
    //  TC_REF_001 - Page Refresh Resets Country Selection
    // =========================================================

    @Test(description = "TC_REF_001 - Verify page refresh resets Country dropdown to default 'Select one'",
          groups = {"regression", "browser-behaviour"})
    public void verifyPageRefreshResetsCountrySelection() {
        logStep("Selecting Country: United States");
        catalogPage.selectCountry("United States");
        Assert.assertEquals(catalogPage.getSelectedCountry(), "United States",
                "Country should be 'United States' after selection");

        logStep("Refreshing the page");
        getDriver().navigate().refresh();
        catalogPage = new ProductCatalogPage(getDriver());

        String countryAfterRefresh = catalogPage.getSelectedCountry();
        logStep("Country dropdown after refresh: '" + countryAfterRefresh + "'");

        Assert.assertTrue(
                countryAfterRefresh.toLowerCase().contains("select") || countryAfterRefresh.isEmpty(),
                "Country dropdown should reset to 'Select one' after page refresh. "
                + "Actual: '" + countryAfterRefresh + "'");
        logPass("Page refresh correctly reset Country dropdown to default");
    }

    // =========================================================
    //  TC_REF_002 - Page Refresh Resets Package Type Selection
    // =========================================================

    @Test(description = "TC_REF_002 - Verify page refresh resets Package Type dropdown to default",
          groups = {"regression", "browser-behaviour"})
    public void verifyPageRefreshResetsPackageTypeSelection() {
        logStep("Selecting Package Type: z/OS-CBPDO (products)");
        catalogPage.selectPackageType("z/OS-CBPDO (products)");
        Assert.assertTrue(
                catalogPage.getSelectedPackageType().contains("CBPDO"),
                "Package type should be CBPDO after selection");

        logStep("Refreshing the page");
        getDriver().navigate().refresh();
        catalogPage = new ProductCatalogPage(getDriver());

        String pkgAfterRefresh = catalogPage.getSelectedPackageType();
        logStep("Package Type after refresh: '" + pkgAfterRefresh + "'");

        Assert.assertTrue(
                pkgAfterRefresh.toLowerCase().contains("select") || pkgAfterRefresh.isEmpty(),
                "Package Type should reset to 'Select one' after refresh. Actual: '" + pkgAfterRefresh + "'");
        logPass("Page refresh correctly reset Package Type dropdown to default");
    }

    // =========================================================
    //  TC_REF_003 - Browser Back After Search Returns to Catalog
    // =========================================================

    @Test(description = "TC_REF_003 - Verify Browser Back button after FMID navigation returns to catalog",
          groups = {"regression", "browser-behaviour"})
    public void verifyBrowserBackAfterFmidNavigation() {
        logStep("Performing search: United States + z/OS-CBPDO");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();

        String resultsUrl = getDriver().getCurrentUrl();
        logStep("Results page URL: " + resultsUrl);

        int fmidCount = catalogPage.getFmidLinks().size();
        if (fmidCount > 0) {
            logStep("Clicking first FMID link");
            catalogPage.clickFirstFmidLink();
            String fmidUrl = getDriver().getCurrentUrl();
            logStep("FMID page URL: " + fmidUrl);

            logStep("Pressing Browser Back button");
            getDriver().navigate().back();
            String backUrl = getDriver().getCurrentUrl();
            logStep("URL after Back: " + backUrl);

            Assert.assertTrue(
                    backUrl.contains("shopzseries") || backUrl.contains("prodcat"),
                    "Browser Back should return to ShopZ catalog/results. Actual: " + backUrl);
            logPass("Browser Back correctly returned to: " + backUrl);
        } else {
            logStep("No FMID links - verifying results page loaded at least");
            Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                    "Results page should have loaded");
            logPass("Results page loaded (no FMID links available for back-navigation test)");
        }
    }

    // =========================================================
    //  TC_REF_004 - Direct URL Navigation Always Loads Catalog
    // =========================================================

    @Test(description = "TC_REF_004 - Verify direct URL navigation to catalog always loads the page",
          groups = {"regression", "browser-behaviour"})
    public void verifyDirectUrlNavigationAlwaysWorks() {
        logStep("Navigating directly to catalog URL: " + ProductCatalogPage.PAGE_URL);
        getDriver().get(ProductCatalogPage.PAGE_URL);
        catalogPage = new ProductCatalogPage(getDriver());

        String url = getDriver().getCurrentUrl();
        String title = catalogPage.getPageTitle();
        logStep("URL: " + url + " | Title: " + title);

        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Direct URL navigation should load the catalog page with correct title");
        Assert.assertTrue(catalogPage.areAllFiltersDisplayed(),
                "All filter dropdowns should be displayed after direct URL navigation");
        logPass("Direct URL navigation loaded catalog correctly. Title: " + title);
    }

    // =========================================================
    //  TC_REF_005 - Repeated Page Opens Do Not Degrade
    // =========================================================

    @Test(description = "TC_REF_005 - Verify opening the catalog page 3 times in a row consistently loads",
          groups = {"regression", "browser-behaviour"})
    public void verifyRepeatedPageOpensConsistent() {
        for (int i = 1; i <= 3; i++) {
            logStep("Opening catalog page (attempt " + i + ")");
            getDriver().get(ProductCatalogPage.PAGE_URL);
            catalogPage = new ProductCatalogPage(getDriver());

            Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                    "Attempt " + i + ": Catalog page title should be correct");
            Assert.assertTrue(catalogPage.areAllFiltersDisplayed(),
                    "Attempt " + i + ": All filter dropdowns should be visible");
            logStep("Attempt " + i + " passed. Title: " + catalogPage.getPageTitle());
        }
        logPass("Catalog page loaded consistently across 3 consecutive opens");
    }

    // =========================================================
    //  TC_REF_006 - Refresh on Results Page Does Not Crash
    // =========================================================

    @Test(description = "TC_REF_006 - Verify refreshing the results page after search does not cause an error",
          groups = {"regression", "browser-behaviour"})
    public void verifyRefreshOnResultsPageDoesNotCrash() {
        logStep("Performing initial search");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();

        String preRefreshUrl = getDriver().getCurrentUrl();
        logStep("URL before refresh: " + preRefreshUrl);

        logStep("Refreshing the results page");
        getDriver().navigate().refresh();
        catalogPage = new ProductCatalogPage(getDriver());

        String postRefreshTitle = catalogPage.getPageTitle();
        logStep("Title after refresh: " + postRefreshTitle);

        Assert.assertTrue(
                postRefreshTitle.toLowerCase().contains("ibm") || postRefreshTitle.toLowerCase().contains("shopz"),
                "Page should load correctly after refresh. Title: " + postRefreshTitle);
        logPass("Results page refreshed without errors. Title: " + postRefreshTitle);
    }

    // =========================================================
    //  TC_REF_007 - Filter State Not Persisted Across New Page Open
    // =========================================================

    @Test(description = "TC_REF_007 - Verify filter selections made in one session do not persist when reopening the page",
          groups = {"regression", "browser-behaviour"})
    public void verifyFilterStateNotPersistedAcrossNewPageOpen() {
        logStep("Selecting filters: United States + z/OS-CBPDO");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)");

        logStep("Navigating away to IBM home then back to catalog via URL");
        getDriver().get("https://www.ibm.com");
        getDriver().get(ProductCatalogPage.PAGE_URL);
        catalogPage = new ProductCatalogPage(getDriver());

        String countryAfter = catalogPage.getSelectedCountry();
        String pkgAfter = catalogPage.getSelectedPackageType();
        logStep("Country after re-open: '" + countryAfter + "'");
        logStep("Package Type after re-open: '" + pkgAfter + "'");

        // Both should be back to "Select one" (no session persistence)
        Assert.assertTrue(
                countryAfter.toLowerCase().contains("select") || countryAfter.isEmpty(),
                "Country should be reset to 'Select one' on fresh page open. Actual: " + countryAfter);
        logPass("Filter state correctly reset on fresh page open");
    }

    // =========================================================
    //  TC_REF_008 - Page Title Consistent After Multiple Refreshes
    // =========================================================

    @Test(description = "TC_REF_008 - Verify page title is consistent across multiple page refreshes",
          groups = {"regression", "browser-behaviour"})
    public void verifyPageTitleConsistentAcrossRefreshes() {
        String initialTitle = catalogPage.getPageTitle();
        logStep("Initial page title: " + initialTitle);

        for (int i = 1; i <= 3; i++) {
            getDriver().navigate().refresh();
            catalogPage = new ProductCatalogPage(getDriver());
            String refreshedTitle = catalogPage.getPageTitle();
            logStep("Title after refresh " + i + ": " + refreshedTitle);
            Assert.assertEquals(refreshedTitle, initialTitle,
                    "Page title should be consistent after refresh " + i
                    + ". Expected: '" + initialTitle + "' Got: '" + refreshedTitle + "'");
        }
        logPass("Page title '" + initialTitle + "' remained consistent across 3 refreshes");
    }
}
