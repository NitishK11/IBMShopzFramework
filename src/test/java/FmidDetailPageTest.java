package com.ibm.shopz.tests;

import com.ibm.shopz.base.BaseTest;
import com.ibm.shopz.pages.FmidDetailPage;
import com.ibm.shopz.pages.ProductCatalogPage;
import com.ibm.shopz.pages.ProductDetailPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * FmidDetailPageTest - TestNG tests for FMID Detail page.
 *
 * Test Coverage (based on IBM ShopZ doc screenshots):
 *  TC_FMID_001 - Click first FMID link and verify new page loads
 *  TC_FMID_002 - Click second FMID link and verify new page loads
 *  TC_FMID_003 - Verify FMID detail page has non-empty title
 *  TC_FMID_004 - Verify product links are present on FMID detail page
 *  TC_FMID_005 - Click product link (e.g. 5655-GOZ) from FMID detail page
 *  TC_FMID_006 - Verify URL changes when navigating from catalog to FMID page
 *  TC_FMID_007 - Navigate back to Product Catalog from FMID detail page
 */
public class FmidDetailPageTest extends BaseTest {

    private ProductCatalogPage catalogPage;

    @BeforeMethod(alwaysRun = true)
    public void initPageAndSearch() {
        logStep("Opening Product Catalog page and performing search");
        catalogPage = new ProductCatalogPage(getDriver());
        catalogPage.open();
        // Perform a search to get results with FMID links
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();
        logStep("Search completed. Ready for FMID link tests.");
    }

    // =========================================================
    //  TC_FMID_001 - Click First FMID Link
    // =========================================================

    @Test(description = "TC_FMID_001 - Click first FMID link from catalog and verify page loads",
          groups = {"regression", "functional"})
    public void clickFirstFmidLinkAndVerifyPageLoad() {
        int fmidCount = catalogPage.getFmidLinks().size();
        logStep("Total FMID links found in results: " + fmidCount);

        if (fmidCount == 0) {
            logStep("No FMID links found - skipping test");
            Assert.fail("No FMID links present in results to test navigation");
        }

        String firstFmidText = catalogPage.getFmidLinks().get(0).getText().trim();
        logStep("Clicking first FMID link: " + firstFmidText);

        FmidDetailPage fmidDetailPage = catalogPage.clickFirstFmidLink();
        String pageUrl = fmidDetailPage.getPageUrl();
        logStep("FMID Detail Page URL: " + pageUrl);

        Assert.assertTrue(fmidDetailPage.isPageLoaded(),
                "FMID detail page should have loaded after clicking FMID link");
        logPass("FMID detail page loaded successfully. URL: " + pageUrl);
    }

    // =========================================================
    //  TC_FMID_002 - Click Second FMID Link
    // =========================================================

    @Test(description = "TC_FMID_002 - Click second FMID link from catalog and verify page loads",
          groups = {"regression", "functional"})
    public void clickSecondFmidLinkAndVerifyPageLoad() {
        int fmidCount = catalogPage.getFmidLinks().size();
        logStep("Total FMID links in results: " + fmidCount);

        if (fmidCount < 2) {
            logStep("Less than 2 FMID links found - skipping test");
            Assert.fail("Need at least 2 FMID links. Found: " + fmidCount);
        }

        String secondFmidText = catalogPage.getFmidLinks().get(1).getText().trim();
        logStep("Clicking second FMID link: " + secondFmidText);

        FmidDetailPage fmidDetailPage = catalogPage.clickSecondFmidLink();
        String pageUrl = fmidDetailPage.getPageUrl();
        logStep("Second FMID Detail Page URL: " + pageUrl);

        Assert.assertTrue(fmidDetailPage.isPageLoaded(),
                "Second FMID detail page should have loaded");
        logPass("Second FMID detail page loaded. URL: " + pageUrl);
    }

    // =========================================================
    //  TC_FMID_003 - FMID Page Has Non-Empty Title
    // =========================================================

    @Test(description = "TC_FMID_003 - Verify FMID detail page has non-empty heading/title",
          groups = {"regression"})
    public void verifyFmidPageHeadingNonEmpty() {
        int fmidCount = catalogPage.getFmidLinks().size();
        if (fmidCount == 0) {
            Assert.fail("No FMID links found for navigation test");
        }

        logStep("Navigating to first FMID detail page");
        FmidDetailPage fmidDetailPage = catalogPage.clickFirstFmidLink();

        String heading = fmidDetailPage.getPageHeadingText();
        logStep("FMID page heading: " + heading);
        Assert.assertFalse(heading.isEmpty(),
                "FMID detail page heading/title should not be empty");
        logPass("FMID detail page heading verified: " + heading);
    }

    // =========================================================
    //  TC_FMID_004 - Product Links Present on FMID Detail Page
    // =========================================================

    @Test(description = "TC_FMID_004 - Verify product links present on FMID detail page",
          groups = {"regression", "functional"})
    public void verifyProductLinksOnFmidPage() {
        int fmidCount = catalogPage.getFmidLinks().size();
        if (fmidCount == 0) {
            Assert.fail("No FMID links found for navigation test");
        }

        logStep("Navigating to first FMID detail page");
        FmidDetailPage fmidDetailPage = catalogPage.clickFirstFmidLink();

        List<String> productLinkTexts = fmidDetailPage.getProductLinkTexts();
        logStep("Product links found on FMID page: " + productLinkTexts);
        Assert.assertFalse(productLinkTexts.isEmpty(),
                "FMID detail page should have at least one product link");
        logPass("Product links found on FMID detail page: " + productLinkTexts);
    }

    // =========================================================
    //  TC_FMID_005 - Navigate to Product Detail from FMID Page
    // =========================================================

    @Test(description = "TC_FMID_005 - Click product link from FMID detail page and verify product detail page",
          groups = {"regression", "functional", "e2e"})
    public void clickProductLinkFromFmidPage() {
        int fmidCount = catalogPage.getFmidLinks().size();
        if (fmidCount == 0) {
            Assert.fail("No FMID links found for navigation test");
        }

        logStep("Navigating to first FMID detail page");
        FmidDetailPage fmidDetailPage = catalogPage.clickFirstFmidLink();

        List<String> productLinkTexts = fmidDetailPage.getProductLinkTexts();
        if (productLinkTexts.isEmpty()) {
            Assert.fail("No product links on FMID detail page to click");
        }

        logStep("Clicking first product link: " + productLinkTexts.get(0));
        ProductDetailPage productDetailPage = fmidDetailPage.clickFirstProductLink();

        String productPageUrl = productDetailPage.getPageUrl();
        logStep("Product detail page URL: " + productPageUrl);

        Assert.assertTrue(productDetailPage.isPageLoaded(),
                "Product detail page should have loaded after clicking product link");
        logPass("Product detail page loaded successfully. URL: " + productPageUrl);
    }

    // =========================================================
    //  TC_FMID_006 - URL Changes After Navigation to FMID Page
    // =========================================================

    @Test(description = "TC_FMID_006 - Verify URL changes when navigating from catalog to FMID detail page",
          groups = {"regression"})
    public void verifyUrlChangesOnFmidNavigation() {
        String catalogUrl = catalogPage.getCurrentUrl();
        logStep("Catalog page URL before navigation: " + catalogUrl);

        int fmidCount = catalogPage.getFmidLinks().size();
        if (fmidCount == 0) {
            Assert.fail("No FMID links available");
        }

        FmidDetailPage fmidDetailPage = catalogPage.clickFirstFmidLink();
        String fmidUrl = fmidDetailPage.getPageUrl();
        logStep("FMID detail page URL: " + fmidUrl);

        Assert.assertNotEquals(fmidUrl, catalogUrl,
                "URL should change after navigating to FMID detail page");
        logPass("URL changed from catalog (" + catalogUrl + ") to FMID page (" + fmidUrl + ")");
    }

    // =========================================================
    //  TC_FMID_007 - Navigate Back to Catalog from FMID Page
    // =========================================================

    @Test(description = "TC_FMID_007 - Navigate back to Product Catalog from FMID detail page",
          groups = {"regression", "navigation"})
    public void navigateBackToCatalogFromFmidPage() {
        int fmidCount = catalogPage.getFmidLinks().size();
        if (fmidCount == 0) {
            Assert.fail("No FMID links available");
        }

        logStep("Navigating to FMID detail page");
        FmidDetailPage fmidDetailPage = catalogPage.clickFirstFmidLink();
        logStep("On FMID page: " + fmidDetailPage.getPageUrl());

        logStep("Navigating back to Product Catalog");
        ProductCatalogPage backToPage = fmidDetailPage.backToProductCatalog();
        String backUrl = backToPage.getCurrentUrl();
        logStep("Back to URL: " + backUrl);

        Assert.assertTrue(
                backUrl.contains("prodcat") || backUrl.contains("shopzseries"),
                "After going back, should be on Product Catalog page. URL: " + backUrl);
        logPass("Successfully navigated back to Product Catalog from FMID detail page");
    }
}
