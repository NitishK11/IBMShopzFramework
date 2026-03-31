package com.ibm.shopz.tests;

import com.ibm.shopz.base.BaseTest;
import com.ibm.shopz.pages.FmidDetailPage;
import com.ibm.shopz.pages.ProductCatalogPage;
import com.ibm.shopz.pages.ProductDetailPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * ProductDetailPageTest - TestNG tests for IBM ShopZ Product Detail page.
 *
 * Test Coverage (based on IBM ShopZ doc screenshot "After Click on Prod Link 5655-GOZ"):
 *  TC_PROD_001 - Navigate to product detail via FMID page and verify page loads
 *  TC_PROD_002 - Verify product detail page has non-empty heading
 *  TC_PROD_003 - Verify product detail page URL is different from FMID page URL
 *  TC_PROD_004 - Navigate back to Product Catalog from Product Detail page
 *  TC_PROD_005 - Verify product links on catalog results can open product detail pages
 *  TC_PROD_006 - E2E: Catalog Search -> FMID Link -> Product Link -> Product Detail
 */
public class ProductDetailPageTest extends BaseTest {

    private ProductCatalogPage catalogPage;

    @BeforeMethod(alwaysRun = true)
    public void initPageAndSearch() {
        logStep("Opening Product Catalog page and performing search");
        catalogPage = new ProductCatalogPage(getDriver());
        catalogPage.open();
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();
        logStep("Search completed. FMID links: " + catalogPage.getFmidLinks().size());
    }

    // =========================================================
    //  TC_PROD_001 - Product Detail Page Loads via FMID
    // =========================================================

    @Test(description = "TC_PROD_001 - Navigate to product detail via FMID link and verify page loads",
          groups = {"regression", "functional"})
    public void verifyProductDetailPageLoadsViaFmid() {
        if (catalogPage.getFmidLinks().isEmpty()) {
            Assert.fail("No FMID links found in catalog results");
        }

        logStep("Clicking first FMID link from catalog results");
        FmidDetailPage fmidDetailPage = catalogPage.clickFirstFmidLink();

        if (fmidDetailPage.getProductLinks().isEmpty()) {
            Assert.fail("No product links found on FMID detail page");
        }

        logStep("Clicking first product link on FMID detail page");
        ProductDetailPage productDetailPage = fmidDetailPage.clickFirstProductLink();

        logStep("Product detail page URL: " + productDetailPage.getPageUrl());
        Assert.assertTrue(productDetailPage.isPageLoaded(),
                "Product detail page should load after clicking product link from FMID page");
        logPass("Product detail page loaded successfully");
    }

    // =========================================================
    //  TC_PROD_002 - Product Detail Page Has Heading
    // =========================================================

    @Test(description = "TC_PROD_002 - Verify product detail page has a non-empty heading",
          groups = {"regression"})
    public void verifyProductDetailPageHeading() {
        if (catalogPage.getFmidLinks().isEmpty()) {
            Assert.fail("No FMID links found");
        }

        logStep("Navigating: Catalog -> FMID -> Product Detail");
        FmidDetailPage fmidDetailPage = catalogPage.clickFirstFmidLink();

        if (fmidDetailPage.getProductLinks().isEmpty()) {
            Assert.fail("No product links on FMID detail page");
        }

        ProductDetailPage productDetailPage = fmidDetailPage.clickFirstProductLink();
        String heading = productDetailPage.getProductNameHeading();
        logStep("Product detail page heading: " + heading);

        Assert.assertFalse(heading.isEmpty(),
                "Product detail page heading should not be empty");
        logPass("Product detail page heading verified: " + heading);
    }

    // =========================================================
    //  TC_PROD_003 - URL Changes from FMID to Product Detail
    // =========================================================

    @Test(description = "TC_PROD_003 - Verify URL changes from FMID page to Product detail page",
          groups = {"regression"})
    public void verifyUrlChangesOnProductDetailNavigation() {
        if (catalogPage.getFmidLinks().isEmpty()) {
            Assert.fail("No FMID links found");
        }

        FmidDetailPage fmidDetailPage = catalogPage.clickFirstFmidLink();
        String fmidUrl = fmidDetailPage.getPageUrl();
        logStep("FMID detail URL: " + fmidUrl);

        if (fmidDetailPage.getProductLinks().isEmpty()) {
            Assert.fail("No product links on FMID detail page");
        }

        ProductDetailPage productDetailPage = fmidDetailPage.clickFirstProductLink();
        String productUrl = productDetailPage.getPageUrl();
        logStep("Product detail URL: " + productUrl);

        Assert.assertNotEquals(productUrl, fmidUrl,
                "URL should change when navigating from FMID page to Product detail page");
        logPass("URL correctly changed from FMID to Product detail: " + productUrl);
    }

    // =========================================================
    //  TC_PROD_004 - Navigate Back to Catalog from Product Detail
    // =========================================================

    @Test(description = "TC_PROD_004 - Navigate back to Product Catalog from Product Detail page",
          groups = {"regression", "navigation"})
    public void navigateBackToCatalogFromProductDetail() {
        if (catalogPage.getFmidLinks().isEmpty()) {
            Assert.fail("No FMID links found");
        }

        FmidDetailPage fmidDetailPage = catalogPage.clickFirstFmidLink();
        if (fmidDetailPage.getProductLinks().isEmpty()) {
            Assert.fail("No product links on FMID detail page");
        }

        ProductDetailPage productDetailPage = fmidDetailPage.clickFirstProductLink();
        logStep("On product detail page: " + productDetailPage.getPageUrl());

        logStep("Navigating back to Product Catalog");
        ProductCatalogPage backToPage = productDetailPage.backToProductCatalog();
        String backUrl = backToPage.getCurrentUrl();
        logStep("Back to URL: " + backUrl);

        Assert.assertTrue(
                backUrl.contains("shopzseries") || backUrl.contains("prodcat"),
                "Should navigate back to Shopz catalog page. Actual URL: " + backUrl);
        logPass("Successfully navigated back to catalog from Product detail page");
    }

    // =========================================================
    //  TC_PROD_005 - Product Links in Catalog Lead to Detail Pages
    // =========================================================

    @Test(description = "TC_PROD_005 - Verify product links in catalog results open product detail pages",
          groups = {"regression", "functional"})
    public void verifyProductLinksInCatalogOpenDetailPages() {
        int productLinksCount = catalogPage.getProductLinks().size();
        logStep("Product links found in catalog results: " + productLinksCount);

        if (productLinksCount == 0) {
            logStep("No direct product links in catalog results - skipping");
            Assert.fail("No product links found in catalog results");
        }

        logStep("Clicking first product link from catalog");
        ProductDetailPage productDetailPage = catalogPage.clickFirstProductLink();
        String productUrl = productDetailPage.getPageUrl();
        logStep("Product detail URL: " + productUrl);

        Assert.assertTrue(productDetailPage.isPageLoaded(),
                "Product detail page should load from catalog product link");
        logPass("Product detail page loaded from catalog link. URL: " + productUrl);
    }

    // =========================================================
    //  TC_PROD_006 - Full E2E: Catalog -> FMID -> Product Detail
    // =========================================================

    @Test(description = "TC_PROD_006 - E2E: Full navigation Catalog Search -> FMID -> Product Detail",
          groups = {"e2e", "regression"})
    public void fullE2ENavigationFlow() {
        logStep("[E2E Step 1] Open Product Catalog page");
        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Catalog page title should be correct");

        logStep("[E2E Step 2] Verify filters are displayed");
        Assert.assertTrue(catalogPage.areAllFiltersDisplayed(),
                "All filter dropdowns should be displayed");

        int fmidCount = catalogPage.getFmidLinks().size();
        logStep("[E2E Step 3] FMID links in results: " + fmidCount);
        if (fmidCount == 0) {
            Assert.fail("No FMID links in catalog results for E2E test");
        }

        logStep("[E2E Step 4] Navigate to first FMID detail page");
        FmidDetailPage fmidDetailPage = catalogPage.clickFirstFmidLink();
        Assert.assertTrue(fmidDetailPage.isPageLoaded(), "FMID detail page should load");
        logStep("FMID page loaded: " + fmidDetailPage.getPageUrl());

        if (fmidDetailPage.getProductLinks().isEmpty()) {
            Assert.fail("No product links on FMID detail page for E2E test");
        }

        logStep("[E2E Step 5] Navigate to Product Detail page");
        ProductDetailPage productDetailPage = fmidDetailPage.clickFirstProductLink();
        Assert.assertTrue(productDetailPage.isPageLoaded(), "Product detail page should load");
        logStep("Product detail page loaded: " + productDetailPage.getPageUrl());

        logPass("[E2E COMPLETE] Full navigation flow: Catalog -> FMID -> Product Detail - PASSED");
    }
}
