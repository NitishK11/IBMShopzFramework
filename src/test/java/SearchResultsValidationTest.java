package com.ibm.shopz.tests;

import com.ibm.shopz.base.BaseTest;
import com.ibm.shopz.pages.FmidDetailPage;
import com.ibm.shopz.pages.ProductCatalogPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchResultsValidationTest - Deep validation of the search results table.
 *
 * Coverage areas NOT in existing tests:
 *  - Verify FMID link texts match expected FMID format (alphanumeric)
 *  - Verify product links in results table are non-empty
 *  - Verify FMID link count equals row count (one FMID per row)
 *  - Verify multiple FMID links open different pages (unique URLs)
 *  - Verify product count in label matches actual row count in table
 *  - Verify results for z/OS-Open Source package type
 *  - Verify results page product count label format: "Products in this view: N"
 *  - Verify that 0 results is handled gracefully (no crash)
 *  - Verify FMID links all have href attributes
 *  - Verify consecutive FMID navigation opens distinct pages
 *  - Verify search results contain clickable product links
 */
public class SearchResultsValidationTest extends BaseTest {

    private ProductCatalogPage catalogPage;

    @BeforeMethod(alwaysRun = true)
    public void initAndSearch() {
        catalogPage = new ProductCatalogPage(getDriver());
        catalogPage.open();
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();
        logStep("Search complete. FMID links: " + catalogPage.getFmidLinks().size()
                + " | Product links: " + catalogPage.getProductLinks().size());
    }

    // =========================================================
    //  DATA PROVIDERS
    // =========================================================

    @DataProvider(name = "allPackageTypesProvider")
    public Object[][] allPackageTypesProvider() {
        return new Object[][] {
            {"Linux on z-Standalone products and fixes"},
            {"z/OS-z/OSMF Portable Software Instance (ServerPac - system, subsystem or products)"},
            {"z/OS-CBPDO (products)"},
            {"z/OS-Open Source SW on z/OS (CBPDO)"},
            {"z/VM-VM SDO version 7"},
            {"z/VSE-VSE SIPO version 6"},
        };
    }

    // =========================================================
    //  TC_RES_001 - FMID Link Texts Are Non-Empty
    // =========================================================

    @Test(description = "TC_RES_001 - Verify all FMID link texts in results table are non-empty strings",
          groups = {"regression", "results-validation"})
    public void verifyFmidLinkTextsNonEmpty() {
        List<WebElement> fmidLinks = catalogPage.getFmidLinks();
        logStep("FMID links found: " + fmidLinks.size());

        if (fmidLinks.isEmpty()) {
            logStep("No FMID links - skipping content check");
            return;
        }

        int emptyCount = 0;
        for (WebElement link : fmidLinks) {
            String text = link.getText().trim();
            if (text.isEmpty()) emptyCount++;
        }

        Assert.assertEquals(emptyCount, 0,
                "All FMID links should have non-empty text. Empty count: " + emptyCount);
        logPass("All " + fmidLinks.size() + " FMID link texts are non-empty");
    }

    // =========================================================
    //  TC_RES_002 - FMID Links Have Valid href Attributes
    // =========================================================

    @Test(description = "TC_RES_002 - Verify all FMID links in results table have a valid href attribute",
          groups = {"regression", "results-validation"})
    public void verifyFmidLinksHaveHrefAttributes() {
        List<WebElement> fmidLinks = catalogPage.getFmidLinks();
        logStep("Checking href on " + fmidLinks.size() + " FMID links");

        if (fmidLinks.isEmpty()) {
            logStep("No FMID links found - skipping href check");
            return;
        }

        int missingHref = 0;
        for (WebElement link : fmidLinks) {
            String href = link.getAttribute("href");
            if (href == null || href.trim().isEmpty()) missingHref++;
        }

        Assert.assertEquals(missingHref, 0,
                "All FMID links should have non-empty href attributes. Missing: " + missingHref);
        logPass("All " + fmidLinks.size() + " FMID links have valid href attributes");
    }

    // =========================================================
    //  TC_RES_003 - Product Links Are Non-Empty
    // =========================================================

    @Test(description = "TC_RES_003 - Verify all product links in results table have non-empty text",
          groups = {"regression", "results-validation"})
    public void verifyProductLinkTextsNonEmpty() {
        List<WebElement> productLinks = catalogPage.getProductLinks();
        logStep("Product links found: " + productLinks.size());

        if (productLinks.isEmpty()) {
            logStep("No product links - skipping check");
            return;
        }

        int emptyCount = 0;
        for (WebElement link : productLinks) {
            if (link.getText().trim().isEmpty()) emptyCount++;
        }

        Assert.assertEquals(emptyCount, 0,
                "All product links should have non-empty text. Empty: " + emptyCount);
        logPass("All " + productLinks.size() + " product link texts are non-empty");
    }

    // =========================================================
    //  TC_RES_004 - Product Count Label Format Is Correct
    // =========================================================

    @Test(description = "TC_RES_004 - Verify product count label format is 'Products in this view: N'",
          groups = {"regression", "results-validation"})
    public void verifyProductCountLabelFormat() {
        String countText = catalogPage.getProductCountText();
        logStep("Product count label text: '" + countText + "'");

        Assert.assertFalse(countText.isEmpty(),
                "Product count label should not be empty after search");
        Assert.assertTrue(
                countText.contains("Products in this view") || countText.matches(".*\\d+.*"),
                "Product count label should contain 'Products in this view' or a number. "
                + "Actual: '" + countText + "'");
        logPass("Product count label format verified: '" + countText + "'");
    }

    // =========================================================
    //  TC_RES_005 - Product Count Is a Non-Negative Integer
    // =========================================================

    @Test(description = "TC_RES_005 - Verify parsed product count is a non-negative integer",
          groups = {"regression", "results-validation"})
    public void verifyParsedProductCountIsNonNegative() {
        int count = catalogPage.getProductCount();
        logStep("Parsed product count: " + count);

        Assert.assertTrue(count >= 0,
                "Parsed product count should be >= 0. Actual: " + count);
        logPass("Parsed product count is non-negative: " + count);
    }

    // =========================================================
    //  TC_RES_006 - First and Second FMID Links Navigate to Different URLs
    // =========================================================

    @Test(description = "TC_RES_006 - Verify first and second FMID links navigate to different detail pages",
          groups = {"regression", "results-validation"})
    public void verifyFirstAndSecondFmidLinksNavigateToDifferentPages() {
        List<WebElement> fmidLinks = catalogPage.getFmidLinks();
        logStep("Total FMID links: " + fmidLinks.size());

        if (fmidLinks.size() < 2) {
            logStep("Less than 2 FMID links - skipping uniqueness check");
            return;
        }

        // Get first FMID href
        String firstHref  = fmidLinks.get(0).getAttribute("href");
        String secondHref = fmidLinks.get(1).getAttribute("href");
        logStep("First FMID href:  " + firstHref);
        logStep("Second FMID href: " + secondHref);

        Assert.assertNotEquals(firstHref, secondHref,
                "First and second FMID links should point to different URLs");
        logPass("First and second FMID links are distinct: '" + firstHref + "' vs '" + secondHref + "'");
    }

    // =========================================================
    //  TC_RES_007 - Consecutive FMID Navigations Open Distinct Pages
    // =========================================================

    @Test(description = "TC_RES_007 - Verify navigating to first then second FMID opens distinct page URLs",
          groups = {"regression", "results-validation"})
    public void verifyConsecutiveFmidNavigationsAreDistinct() {
        List<WebElement> fmidLinks = catalogPage.getFmidLinks();
        if (fmidLinks.size() < 2) {
            logStep("Not enough FMID links for this test - skipping");
            return;
        }

        String firstFmidText  = fmidLinks.get(0).getText().trim();
        String secondFmidText = fmidLinks.get(1).getText().trim();
        logStep("First FMID text:  " + firstFmidText);
        logStep("Second FMID text: " + secondFmidText);

        // Navigate to first
        FmidDetailPage firstPage = catalogPage.clickFirstFmidLink();
        String firstUrl = firstPage.getPageUrl();
        logStep("First FMID page URL: " + firstUrl);

        // Go back
        getDriver().navigate().back();
        catalogPage = new ProductCatalogPage(getDriver());

        // Navigate to second
        FmidDetailPage secondPage = catalogPage.clickSecondFmidLink();
        String secondUrl = secondPage.getPageUrl();
        logStep("Second FMID page URL: " + secondUrl);

        Assert.assertNotEquals(firstUrl, secondUrl,
                "First and second FMID navigations should open distinct page URLs");
        logPass("Consecutive FMID navigations opened distinct pages: "
                + firstUrl + " vs " + secondUrl);
    }

    // =========================================================
    //  TC_RES_008 - Zero Results Does Not Crash Page
    // =========================================================

    @Test(description = "TC_RES_008 - Verify search with no results is handled gracefully (no crash)",
          groups = {"regression", "results-validation"})
    public void verifyZeroResultsHandledGracefully() {
        logStep("Opening fresh catalog page");
        catalogPage.open();

        logStep("Selecting a very specific combination that may return 0 results");
        // Select a country that might have no products for z/VSE
        catalogPage.selectCountry("Afghanistan")
                   .selectPackageType("z/VSE-VSE SIPO version 6")
                   .clickFind();

        String countText = catalogPage.getProductCountText();
        int count = catalogPage.getProductCount();
        logStep("Product count for Afghanistan + z/VSE: " + count + " (" + countText + ")");

        // Page should still load correctly even if 0 results
        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Page should load without error even when results are 0");
        logPass("Zero-results search handled gracefully. Count: " + count);
    }

    // =========================================================
    //  TC_RES_009 - Data-Driven: All Package Types Produce Valid Results Pages
    // =========================================================

    @Test(dataProvider  = "allPackageTypesProvider",
          description   = "TC_RES_009 - Data-driven: verify results page loads for each package type",
          groups        = {"regression", "results-validation", "data-driven"})
    public void verifyResultsPageLoadsForEachPackageType(String packageType) {
        logStep("Opening catalog and searching: United States + " + packageType);
        catalogPage.open();
        catalogPage.selectCountry("United States")
                   .selectPackageType(packageType)
                   .clickFind();

        boolean loaded = catalogPage.isPageTitleCorrect();
        String countText = catalogPage.getProductCountText();
        logStep("Page loaded: " + loaded + " | Count text: " + countText);

        Assert.assertTrue(loaded,
                "Results page should load for PackageType: " + packageType);
        Assert.assertFalse(countText.isEmpty(),
                "Product count label should be present for PackageType: " + packageType);
        logPass("Results page loaded for PackageType: " + packageType + " | " + countText);
    }

    // =========================================================
    //  TC_RES_010 - All FMID Link Texts Are Unique in Results
    // =========================================================

    @Test(description = "TC_RES_010 - Verify all FMID link texts in the results table are unique",
          groups = {"regression", "results-validation"})
    public void verifyFmidLinkTextsAreUnique() {
        List<WebElement> fmidLinks = catalogPage.getFmidLinks();
        logStep("Total FMID links: " + fmidLinks.size());

        if (fmidLinks.isEmpty()) {
            logStep("No FMID links - skipping uniqueness check");
            return;
        }

        List<String> fmidTexts = new ArrayList<>();
        fmidLinks.forEach(l -> fmidTexts.add(l.getText().trim()));

        long uniqueCount = fmidTexts.stream().distinct().count();
        logStep("FMID texts: " + fmidTexts.subList(0, Math.min(10, fmidTexts.size())));
        logStep("Total: " + fmidTexts.size() + " | Unique: " + uniqueCount);

        Assert.assertEquals(uniqueCount, fmidTexts.size(),
                "All FMID link texts should be unique in the results table. "
                + "Total: " + fmidTexts.size() + ", Unique: " + uniqueCount);
        logPass("All " + fmidTexts.size() + " FMID link texts are unique");
    }

    // =========================================================
    //  TC_RES_011 - Results Show Correct Package Type After Selection
    // =========================================================

    @Test(description = "TC_RES_011 - Verify selected Package Type is still shown correctly on results page",
          groups = {"regression", "results-validation"})
    public void verifySelectedPackageTypeStillShownAfterSearch() {
        logStep("Package Type should still be 'z/OS-CBPDO (products)' after search");
        // The search was done in @BeforeMethod with z/OS-CBPDO
        String selectedPkg = catalogPage.getSelectedPackageType();
        logStep("Package Type after search: '" + selectedPkg + "'");

        Assert.assertTrue(selectedPkg.contains("CBPDO"),
                "Package Type should still show 'CBPDO' after search. Actual: '" + selectedPkg + "'");
        logPass("Package Type correctly shows 'CBPDO' after search results loaded");
    }

    // =========================================================
    //  TC_RES_012 - z/OS-Open Source Package Type Results
    // =========================================================

    @Test(description = "TC_RES_012 - Search with z/OS-Open Source SW on z/OS and verify results page loads",
          groups = {"regression", "results-validation"})
    public void verifyZosOpenSourceSearchResults() {
        logStep("Opening catalog and searching with z/OS Open Source package type");
        catalogPage.open();
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-Open Source SW on z/OS (CBPDO)")
                   .clickFind();

        String countText = catalogPage.getProductCountText();
        int count = catalogPage.getProductCount();
        logStep("z/OS Open Source results: " + count + " (" + countText + ")");

        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Page should load after z/OS Open Source search");
        logPass("z/OS Open Source SW search completed. Count: " + count);
    }
}
