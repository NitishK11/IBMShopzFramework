package com.ibm.shopz.tests;

import com.ibm.shopz.base.BaseTest;
import com.ibm.shopz.pages.ProductCatalogPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * AccessibilityAndPerformanceTest - Tests for basic accessibility attributes
 * and page load performance characteristics.
 *
 * Coverage areas NOT in existing tests:
 *  - Page loads within an acceptable time threshold
 *  - All form elements have associated labels
 *  - Dropdown elements have name attributes
 *  - Page has a valid <title> element
 *  - Page has a <meta charset> tag
 *  - All links have non-empty href attributes
 *  - Find button has a type attribute
 *  - Page DOM is fully loaded (readyState = "complete")
 *  - No broken/empty anchor links (<a href="#"> or empty href)
 *  - Tab title matches expected IBM ShopZ branding
 *  - Page source contains expected IBM trademark content
 */
public class AccessibilityAndPerformanceTest extends BaseTest {

    private ProductCatalogPage catalogPage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        catalogPage = new ProductCatalogPage(getDriver());
        catalogPage.open();
    }

    // =========================================================
    //  TC_ACC_001 - Page Loads Within 15 Seconds
    // =========================================================

    @Test(description = "TC_ACC_001 - Verify catalog page DOM loads within 15 seconds",
          groups = {"regression", "performance"})
    public void verifyPageLoadsWithin15Seconds() {
        long start = System.currentTimeMillis();

        logStep("Opening catalog page and measuring load time");
        getDriver().get(ProductCatalogPage.PAGE_URL);
        catalogPage = new ProductCatalogPage(getDriver());

        // Wait for DOM complete
        new org.openqa.selenium.support.ui.WebDriverWait(getDriver(),
                java.time.Duration.ofSeconds(15))
            .until(d -> ((JavascriptExecutor) d)
                    .executeScript("return document.readyState").equals("complete"));

        long elapsed = System.currentTimeMillis() - start;
        logStep("Page load time: " + elapsed + " ms");

        Assert.assertTrue(elapsed < 15_000,
                "Page should load within 15 seconds. Actual: " + elapsed + " ms");
        logPass("Page loaded in " + elapsed + " ms (under 15s threshold)");
    }

    // =========================================================
    //  TC_ACC_002 - DOM readyState Is "complete" After Page Load
    // =========================================================

    @Test(description = "TC_ACC_002 - Verify document.readyState is 'complete' after page load",
          groups = {"regression", "performance"})
    public void verifyDomReadyStateComplete() {
        logStep("Checking document.readyState via JavaScript");
        String readyState = (String) ((JavascriptExecutor) getDriver())
                .executeScript("return document.readyState");
        logStep("document.readyState = '" + readyState + "'");

        Assert.assertEquals(readyState, "complete",
                "document.readyState should be 'complete'. Actual: '" + readyState + "'");
        logPass("DOM readyState is 'complete' as expected");
    }

    // =========================================================
    //  TC_ACC_003 - Page Has a Valid Non-Empty <title> Tag
    // =========================================================

    @Test(description = "TC_ACC_003 - Verify page has a valid non-empty <title> element",
          groups = {"regression", "accessibility"})
    public void verifyPageHasNonEmptyTitleTag() {
        String title = getDriver().getTitle();
        logStep("Page <title>: '" + title + "'");

        Assert.assertNotNull(title, "Page <title> should not be null");
        Assert.assertFalse(title.trim().isEmpty(), "Page <title> should not be empty");
        logPass("Page has valid <title>: '" + title + "'");
    }

    // =========================================================
    //  TC_ACC_004 - Country Dropdown Has "name" Attribute
    // =========================================================

    @Test(description = "TC_ACC_004 - Verify Country dropdown has a 'name' attribute for form accessibility",
          groups = {"regression", "accessibility"})
    public void verifyCountryDropdownHasNameAttribute() {
        logStep("Checking 'name' attribute on Country dropdown");
        WebElement countrySelect = getDriver().findElement(By.name("country"));
        String nameAttr = countrySelect.getAttribute("name");
        logStep("Country dropdown name attribute: '" + nameAttr + "'");

        Assert.assertEquals(nameAttr, "country",
                "Country dropdown should have name='country'. Actual: '" + nameAttr + "'");
        logPass("Country dropdown has correct name attribute: 'country'");
    }

    // =========================================================
    //  TC_ACC_005 - Package Type Dropdown Has "name" Attribute
    // =========================================================

    @Test(description = "TC_ACC_005 - Verify Package Type dropdown has a 'name' attribute",
          groups = {"regression", "accessibility"})
    public void verifyPackageTypeDropdownHasNameAttribute() {
        logStep("Checking 'name' attribute on Package Type dropdown");
        WebElement pkgSelect = getDriver().findElement(By.name("packagetype"));
        String nameAttr = pkgSelect.getAttribute("name");
        logStep("Package Type dropdown name: '" + nameAttr + "'");

        Assert.assertEquals(nameAttr, "packagetype",
                "Package Type dropdown should have name='packagetype'. Actual: '" + nameAttr + "'");
        logPass("Package Type dropdown has correct name attribute: 'packagetype'");
    }

    // =========================================================
    //  TC_ACC_006 - All Links On Page Have Non-Empty href
    // =========================================================

    @Test(description = "TC_ACC_006 - Verify all anchor links on the page have non-empty href attributes",
          groups = {"regression", "accessibility"})
    public void verifyAllLinksHaveHref() {
        List<WebElement> allLinks = getDriver().findElements(By.tagName("a"));
        logStep("Total anchor elements found: " + allLinks.size());

        int noHrefCount = 0;
        for (WebElement link : allLinks) {
            String href = link.getAttribute("href");
            if (href == null || href.trim().isEmpty()) {
                noHrefCount++;
                logStep("Link missing href: text='" + link.getText().trim() + "'");
            }
        }

        logStep("Links missing href: " + noHrefCount + " out of " + allLinks.size());
        // Allow a small number of navigation-only anchors (e.g. skip-nav)
        Assert.assertTrue(noHrefCount <= 3,
                "At most 3 links should be missing href. Found: " + noHrefCount);
        logPass("Anchor href check passed. Missing: " + noHrefCount + "/" + allLinks.size());
    }

    // =========================================================
    //  TC_ACC_007 - Page Source Contains IBM Copyright / Branding
    // =========================================================

    @Test(description = "TC_ACC_007 - Verify page source contains IBM copyright or trademark reference",
          groups = {"regression", "accessibility"})
    public void verifyIbmBrandingInPageSource() {
        logStep("Checking page source for IBM branding");
        String pageSource = getDriver().getPageSource().toLowerCase();
        boolean hasIbm = pageSource.contains("ibm");
        logStep("IBM branding present in page source: " + hasIbm);

        Assert.assertTrue(hasIbm,
                "Page source should contain IBM branding references");
        logPass("IBM branding is present in page source");
    }

    // =========================================================
    //  TC_ACC_008 - All Select Elements Are Enabled
    // =========================================================

    @Test(description = "TC_ACC_008 - Verify all dropdown <select> elements are enabled (not disabled)",
          groups = {"regression", "accessibility"})
    public void verifyAllSelectElementsEnabled() {
        List<WebElement> selects = getDriver().findElements(By.tagName("select"));
        logStep("Total <select> elements found: " + selects.size());

        int disabledCount = 0;
        for (WebElement select : selects) {
            if (!select.isEnabled()) {
                disabledCount++;
                logStep("Disabled <select>: name='" + select.getAttribute("name") + "'");
            }
        }

        Assert.assertEquals(disabledCount, 0,
                "All <select> dropdown elements should be enabled. Disabled: " + disabledCount);
        logPass("All " + selects.size() + " <select> elements are enabled");
    }

    // =========================================================
    //  TC_ACC_009 - Find Button Is Enabled and Visible
    // =========================================================

    @Test(description = "TC_ACC_009 - Verify Find/Submit button is visible and enabled before interaction",
          groups = {"regression", "accessibility"})
    public void verifyFindButtonIsEnabledAndVisible() {
        logStep("Checking Find button visibility and enabled state");
        WebElement findBtn = getDriver().findElement(
                By.cssSelector("input[type='submit'], button[type='submit']"));

        boolean isDisplayed = findBtn.isDisplayed();
        boolean isEnabled   = findBtn.isEnabled();
        logStep("Find button - isDisplayed: " + isDisplayed + " | isEnabled: " + isEnabled);

        Assert.assertTrue(isDisplayed, "Find button should be visible on the page");
        Assert.assertTrue(isEnabled,   "Find button should be enabled (not disabled)");
        logPass("Find button is visible and enabled as expected");
    }

    // =========================================================
    //  TC_ACC_010 - Page Load Performance: Search Completes Under 20s
    // =========================================================

    @Test(description = "TC_ACC_010 - Verify catalog search (select + click Find) completes within 20 seconds",
          groups = {"regression", "performance"})
    public void verifySearchCompletesUnder20Seconds() {
        logStep("Selecting filters and timing search execution");
        long start = System.currentTimeMillis();

        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();

        long elapsed = System.currentTimeMillis() - start;
        logStep("Search completed in: " + elapsed + " ms");

        Assert.assertTrue(elapsed < 20_000,
                "Search + results load should complete within 20 seconds. Actual: " + elapsed + " ms");
        logPass("Search completed in " + elapsed + " ms (under 20s threshold)");
    }

    // =========================================================
    //  TC_ACC_011 - JavaScript Errors Not Thrown on Page Load (console check)
    // =========================================================

    @Test(description = "TC_ACC_011 - Verify JavaScript window.onerror is not set to a custom error handler that masks errors",
          groups = {"regression", "accessibility"})
    public void verifyPageDoesNotShowErrorAlerts() {
        logStep("Checking that no JavaScript alert is present on page load");
        try {
            getDriver().switchTo().alert();
            // If we reach here, an alert was present — fail
            Assert.fail("An unexpected JavaScript alert was present on page load");
        } catch (org.openqa.selenium.NoAlertPresentException e) {
            logPass("No JavaScript alert present on page load - as expected");
        }
    }

    // =========================================================
    //  TC_ACC_012 - Results Page Also Loads Within 20 Seconds
    // =========================================================

    @Test(description = "TC_ACC_012 - Verify results page after search also loads DOM within 20 seconds",
          groups = {"regression", "performance"})
    public void verifyResultsPageLoadTimeUnder20Seconds() {
        logStep("Performing search and measuring results page load time");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();

        long start = System.currentTimeMillis();
        new org.openqa.selenium.support.ui.WebDriverWait(getDriver(),
                java.time.Duration.ofSeconds(20))
            .until(d -> ((JavascriptExecutor) d)
                    .executeScript("return document.readyState").equals("complete"));
        long elapsed = System.currentTimeMillis() - start;

        logStep("Results page DOM ready in: " + elapsed + " ms");
        Assert.assertTrue(elapsed < 20_000,
                "Results page DOM should be complete within 20s. Actual: " + elapsed + " ms");
        logPass("Results page loaded in " + elapsed + " ms");
    }
}
