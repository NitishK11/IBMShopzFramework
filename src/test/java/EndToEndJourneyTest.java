package com.ibm.shopz.tests;

import com.ibm.shopz.base.BaseTest;
import com.ibm.shopz.pages.FmidDetailPage;
import com.ibm.shopz.pages.LoginPage;
import com.ibm.shopz.pages.ProductCatalogPage;
import com.ibm.shopz.pages.ProductDetailPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * EndToEndJourneyTest - Full multi-step user journey tests that simulate real
 * customer workflows through IBM ShopZ.
 *
 * Coverage areas NOT in existing tests:
 *  - Journey: Open catalog -> FAQ -> back to catalog -> search -> FMID detail
 *  - Journey: Open catalog -> Glossary -> back -> search -> product detail
 *  - Journey: Open catalog -> search for z/VM -> first FMID -> product detail -> back to catalog
 *  - Journey: Open catalog -> search for Linux -> change to z/VSE -> verify new results
 *  - Journey: Open catalog -> sign in link -> verify login page -> browser back -> catalog
 *  - Journey: Open catalog -> search all 6 package types sequentially
 *  - Journey: Open catalog -> search -> get FMID list -> navigate each of first 3 FMIDs
 */
public class EndToEndJourneyTest extends BaseTest {

    private ProductCatalogPage catalogPage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        catalogPage = new ProductCatalogPage(getDriver());
        catalogPage.open();
    }

    // =========================================================
    //  TC_E2E_001 - Journey: Catalog -> FAQ -> Back -> Search -> FMID Detail
    // =========================================================

    @Test(description = "TC_E2E_001 - Journey: Catalog -> FAQ page -> back -> search -> FMID detail page",
          groups = {"e2e", "regression"})
    public void journeyCatalogFaqBackSearchFmid() {
        logStep("[Step 1] Verify catalog page loaded");
        Assert.assertTrue(catalogPage.isPageTitleCorrect(), "Catalog page should be loaded");

        logStep("[Step 2] Click FAQs link");
        catalogPage.clickFAQs();
        String faqUrl = getDriver().getCurrentUrl();
        logStep("FAQ URL: " + faqUrl);
        Assert.assertTrue(faqUrl.contains("faq"), "Should be on FAQ page. URL: " + faqUrl);

        logStep("[Step 3] Navigate back to catalog");
        getDriver().navigate().back();
        catalogPage = new ProductCatalogPage(getDriver());
        Assert.assertTrue(catalogPage.isPageTitleCorrect(), "Should be back on catalog page");

        logStep("[Step 4] Perform search");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();
        logStep("Search results loaded. FMID count: " + catalogPage.getFmidLinks().size());

        logStep("[Step 5] Navigate to first FMID if available");
        if (!catalogPage.getFmidLinks().isEmpty()) {
            FmidDetailPage fmidPage = catalogPage.clickFirstFmidLink();
            Assert.assertTrue(fmidPage.isPageLoaded(), "FMID detail page should load");
            logStep("FMID detail page URL: " + fmidPage.getPageUrl());
        } else {
            logStep("No FMID links available - journey ends at search results");
        }

        logPass("[E2E_001 COMPLETE] Catalog -> FAQ -> Back -> Search -> FMID detail");
    }

    // =========================================================
    //  TC_E2E_002 - Journey: Catalog -> Glossary -> Back -> Search -> Product Detail
    // =========================================================

    @Test(description = "TC_E2E_002 - Journey: Catalog -> Glossary -> back -> search -> product detail",
          groups = {"e2e", "regression"})
    public void journeyCatalogGlossaryBackSearchProductDetail() {
        logStep("[Step 1] Verify catalog page loaded");
        Assert.assertTrue(catalogPage.isPageTitleCorrect(), "Catalog page should be loaded");

        logStep("[Step 2] Click Glossary link");
        catalogPage.clickGlossary();
        String glossaryUrl = getDriver().getCurrentUrl();
        logStep("Glossary URL: " + glossaryUrl);
        Assert.assertTrue(glossaryUrl.contains("glossary"),
                "Should be on Glossary page. URL: " + glossaryUrl);

        logStep("[Step 3] Navigate back to catalog");
        getDriver().navigate().back();
        catalogPage = new ProductCatalogPage(getDriver());
        Assert.assertTrue(catalogPage.isPageTitleCorrect(), "Should be back on catalog page");

        logStep("[Step 4] Perform search");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();

        logStep("[Step 5] Navigate to product detail via FMID");
        if (!catalogPage.getFmidLinks().isEmpty()) {
            FmidDetailPage fmidPage = catalogPage.clickFirstFmidLink();
            if (!fmidPage.getProductLinks().isEmpty()) {
                ProductDetailPage productPage = fmidPage.clickFirstProductLink();
                Assert.assertTrue(productPage.isPageLoaded(), "Product detail page should load");
                logStep("Product detail URL: " + productPage.getPageUrl());
            } else {
                logStep("No product links on FMID page");
            }
        } else {
            logStep("No FMID links available");
        }

        logPass("[E2E_002 COMPLETE] Catalog -> Glossary -> Back -> Search -> Product Detail");
    }

    // =========================================================
    //  TC_E2E_003 - Journey: z/VM Search -> FMID -> Product -> Back to Catalog
    // =========================================================

    @Test(description = "TC_E2E_003 - Journey: z/VM search -> FMID detail -> product detail -> back to catalog",
          groups = {"e2e", "regression"})
    public void journeyZvmSearchToProductAndBackToCatalog() {
        logStep("[Step 1] Select z/VM package type and search");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/VM-VM SDO version 7")
                   .clickFind();

        int fmidCount = catalogPage.getFmidLinks().size();
        logStep("[Step 2] FMID links found for z/VM: " + fmidCount);

        if (fmidCount > 0) {
            logStep("[Step 3] Navigate to first FMID detail page");
            FmidDetailPage fmidPage = catalogPage.clickFirstFmidLink();
            Assert.assertTrue(fmidPage.isPageLoaded(), "z/VM FMID detail page should load");

            if (!fmidPage.getProductLinks().isEmpty()) {
                logStep("[Step 4] Navigate to product detail page");
                ProductDetailPage productPage = fmidPage.clickFirstProductLink();
                Assert.assertTrue(productPage.isPageLoaded(), "Product detail page should load");
                logStep("Product page: " + productPage.getPageUrl());

                logStep("[Step 5] Navigate back to catalog");
                ProductCatalogPage backPage = productPage.backToProductCatalog();
                Assert.assertTrue(backPage.isPageTitleCorrect(),
                        "Should return to IBM ShopZ catalog page");
                logStep("Back to: " + backPage.getCurrentUrl());
            } else {
                logStep("[Step 4] No product links on z/VM FMID page");
            }
        } else {
            logStep("[Step 3] No FMID links for z/VM - verifying results page only");
            Assert.assertTrue(catalogPage.isPageTitleCorrect(), "Results page should load for z/VM");
        }

        logPass("[E2E_003 COMPLETE] z/VM Search -> FMID -> Product -> Back to Catalog");
    }

    // =========================================================
    //  TC_E2E_004 - Journey: Linux Search -> Change to z/VSE -> New Results
    // =========================================================

    @Test(description = "TC_E2E_004 - Journey: Search Linux -> change to z/VSE -> verify new results page",
          groups = {"e2e", "regression"})
    public void journeyLinuxSearchThenSwitchToZvse() {
        logStep("[Step 1] Search with Linux package type");
        catalogPage.selectCountry("United States")
                   .selectPackageType("Linux on z-Standalone products and fixes")
                   .clickFind();

        String linuxCountText = catalogPage.getProductCountText();
        logStep("[Step 2] Linux results count: " + linuxCountText);

        logStep("[Step 3] Change Package Type to z/VSE and re-search");
        catalogPage.selectPackageType("z/VSE-VSE SIPO version 6")
                   .clickFind();

        String zvseCountText = catalogPage.getProductCountText();
        logStep("[Step 4] z/VSE results count: " + zvseCountText);

        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Page should load correctly after switching from Linux to z/VSE");
        logPass("[E2E_004 COMPLETE] Linux: " + linuxCountText + " -> z/VSE: " + zvseCountText);
    }

    // =========================================================
    //  TC_E2E_005 - Journey: Sign In Link -> Login Page -> Back to Catalog
    // =========================================================

    @Test(description = "TC_E2E_005 - Journey: Catalog -> click Sign In -> verify login page -> browser back -> catalog",
          groups = {"e2e", "regression"})
    public void journeySignInLinkLoginPageBackToCatalog() {
        logStep("[Step 1] On catalog page");
        String catalogUrl = catalogPage.getCurrentUrl();
        Assert.assertTrue(catalogPage.isPageTitleCorrect(), "Catalog page should be loaded");

        logStep("[Step 2] Click Sign In / Register link");
        catalogPage.clickSignInRegister();
        String loginUrl = getDriver().getCurrentUrl();
        logStep("[Step 3] Login page URL: " + loginUrl);

        Assert.assertTrue(
                loginUrl.contains("login") || loginUrl.contains("ShopzSeries.wss"),
                "Should be on login page. URL: " + loginUrl);

        logStep("[Step 4] Press browser Back");
        getDriver().navigate().back();
        catalogPage = new ProductCatalogPage(getDriver());
        String backUrl = catalogPage.getCurrentUrl();
        logStep("[Step 5] URL after Back: " + backUrl);

        Assert.assertTrue(
                backUrl.contains("shopzseries") || backUrl.contains("prodcat"),
                "Should return to catalog after Browser Back. URL: " + backUrl);
        logPass("[E2E_005 COMPLETE] Catalog -> Sign In -> Back to Catalog");
    }

    // =========================================================
    //  TC_E2E_006 - Journey: Search All 6 Package Types Sequentially
    // =========================================================

    @Test(description = "TC_E2E_006 - Journey: Search with all 6 package types sequentially and verify each completes",
          groups = {"e2e", "regression"})
    public void journeySearchAllSixPackageTypesSequentially() {
        String[] packageTypes = {
            "Linux on z-Standalone products and fixes",
            "z/OS-z/OSMF Portable Software Instance (ServerPac - system, subsystem or products)",
            "z/OS-CBPDO (products)",
            "z/OS-Open Source SW on z/OS (CBPDO)",
            "z/VM-VM SDO version 7",
            "z/VSE-VSE SIPO version 6"
        };

        for (int i = 0; i < packageTypes.length; i++) {
            logStep("[Package Type " + (i + 1) + "/" + packageTypes.length + "] "
                    + "Searching: " + packageTypes[i]);

            catalogPage.open();
            catalogPage.selectCountry("United States")
                       .selectPackageType(packageTypes[i])
                       .clickFind();

            String countText = catalogPage.getProductCountText();
            boolean pageLoaded = catalogPage.isPageTitleCorrect();
            logStep("  -> Page loaded: " + pageLoaded + " | Count: " + countText);

            Assert.assertTrue(pageLoaded,
                    "Page should load for package type: " + packageTypes[i]);
        }

        logPass("[E2E_006 COMPLETE] All 6 package types searched sequentially - all passed");
    }

    // =========================================================
    //  TC_E2E_007 - Journey: Navigate First 3 FMID Links Sequentially
    // =========================================================

    @Test(description = "TC_E2E_007 - Journey: Navigate to first 3 FMID links from results and verify each loads",
          groups = {"e2e", "regression"})
    public void journeyNavigateFirst3FmidLinksSequentially() {
        logStep("[Step 1] Perform search");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();

        List<org.openqa.selenium.WebElement> fmidLinks = catalogPage.getFmidLinks();
        int linksToTest = Math.min(3, fmidLinks.size());
        logStep("[Step 2] Total FMID links: " + fmidLinks.size() + " | Testing first: " + linksToTest);

        if (linksToTest == 0) {
            logStep("No FMID links available - skipping sequential navigation");
            return;
        }

        // Collect the href URLs first (to avoid stale element refs after navigations)
        List<String> fmidHrefs = new java.util.ArrayList<>();
        for (int i = 0; i < linksToTest; i++) {
            fmidHrefs.add(fmidLinks.get(i).getAttribute("href"));
        }

        for (int i = 0; i < linksToTest; i++) {
            logStep("[Step " + (i + 3) + "] Navigating to FMID " + (i + 1) + ": " + fmidHrefs.get(i));
            getDriver().get(fmidHrefs.get(i));

            FmidDetailPage fmidPage = new FmidDetailPage(getDriver());
            boolean loaded = fmidPage.isPageLoaded();
            logStep("  -> FMID page " + (i + 1) + " loaded: " + loaded
                    + " | URL: " + fmidPage.getPageUrl());
            Assert.assertTrue(loaded, "FMID detail page " + (i + 1) + " should load. URL: " + fmidHrefs.get(i));
        }

        logPass("[E2E_007 COMPLETE] First " + linksToTest + " FMID pages all loaded successfully");
    }

    // =========================================================
    //  TC_E2E_008 - Journey: Full Round Trip - Catalog -> Search -> FMID -> Product -> Catalog
    // =========================================================

    @Test(description = "TC_E2E_008 - Journey: Full round trip - Catalog -> Search -> FMID -> Product -> back to Catalog",
          groups = {"e2e", "regression"})
    public void journeyFullRoundTripCatalogToProductAndBack() {
        logStep("[Step 1] Open catalog and verify");
        Assert.assertTrue(catalogPage.isPageTitleCorrect(), "Catalog should load");
        Assert.assertTrue(catalogPage.areAllFiltersDisplayed(), "All filters should display");
        String catalogUrl = catalogPage.getCurrentUrl();

        logStep("[Step 2] Search: United States + z/OS-CBPDO");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();
        String countText = catalogPage.getProductCountText();
        logStep("Results: " + countText);

        if (catalogPage.getFmidLinks().isEmpty()) {
            logStep("No FMID links - round trip ends at search results");
            logPass("[E2E_008 PARTIAL] Catalog -> Search (no FMID links available)");
            return;
        }

        logStep("[Step 3] Navigate to FMID detail page");
        FmidDetailPage fmidPage = catalogPage.clickFirstFmidLink();
        Assert.assertTrue(fmidPage.isPageLoaded(), "FMID page should load");

        if (fmidPage.getProductLinks().isEmpty()) {
            logStep("No product links on FMID page - going back to catalog");
            fmidPage.backToProductCatalog();
            logPass("[E2E_008 PARTIAL] Catalog -> Search -> FMID (no product links)");
            return;
        }

        logStep("[Step 4] Navigate to product detail page");
        ProductDetailPage productPage = fmidPage.clickFirstProductLink();
        Assert.assertTrue(productPage.isPageLoaded(), "Product detail page should load");
        logStep("Product detail: " + productPage.getPageUrl());

        logStep("[Step 5] Navigate back to catalog");
        ProductCatalogPage finalCatalog = productPage.backToProductCatalog();
        Assert.assertTrue(finalCatalog.isPageTitleCorrect(), "Should end up on catalog page");
        logStep("Final catalog URL: " + finalCatalog.getCurrentUrl());

        logPass("[E2E_008 COMPLETE] Full round trip: Catalog -> Search -> FMID -> Product -> Catalog");
    }
}
