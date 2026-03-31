package com.ibm.shopz.tests;

import com.ibm.shopz.base.BaseTest;
import com.ibm.shopz.pages.ProductCatalogPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * ProductCatalogPageTest - TestNG tests for IBM ShopZ Product Catalog page.
 *
 * Test Coverage (based on IBM ShopZ doc screenshots):
 *  TC_001 - Verify page loads and title is correct
 *  TC_002 - Verify all four filter dropdowns are displayed
 *  TC_003 - Verify NOTE message is displayed on page
 *  TC_004 - Verify Country dropdown contains multiple options (including "United States")
 *  TC_005 - Verify all 6 Package Type options are present (as per doc)
 *  TC_006 - Verify Group dropdown is displayed and has options
 *  TC_007 - Verify Language dropdown has options (including English)
 *  TC_008 - Select country "United States" and verify selection
 *  TC_009 - Select Package Type "z/OS-CBPDO (products)" and click Find
 *  TC_010 - Select Package Type "Linux on z-Standalone products and fixes" and click Find
 *  TC_011 - Select Package Type "z/VM-VM SDO version 7" and click Find
 *  TC_012 - Select Package Type "z/VSE-VSE SIPO version 6" and click Find
 *  TC_013 - Verify product count label is displayed after search
 *  TC_014 - Verify results table appears after a valid search
 *  TC_015 - Verify FMID links are present after a valid search
 */
public class ProductCatalogPageTest extends BaseTest {

    private ProductCatalogPage catalogPage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        catalogPage = new ProductCatalogPage(getDriver());
        catalogPage.open();
    }

    // =========================================================
    //  TC_001 - Page Title
    // =========================================================

    @Test(description = "TC_001 - Verify Product Catalog page loads with correct title",
          groups = {"smoke", "regression"})
    public void verifyPageTitle() {
        logStep("Verifying page title contains 'Shopz' or 'IBM'");
        String title = catalogPage.getPageTitle();
        logStep("Actual page title: " + title);
        Assert.assertTrue(
                title.toLowerCase().contains("shopz") || title.toLowerCase().contains("ibm"),
                "Page title should contain 'Shopz' or 'IBM'. Actual: " + title);
        logPass("Page title verified: " + title);
    }

    // =========================================================
    //  TC_002 - All Filters Displayed
    // =========================================================

    @Test(description = "TC_002 - Verify all four filter dropdowns are displayed",
          groups = {"smoke", "regression"})
    public void verifyAllFiltersDisplayed() {
        logStep("Checking visibility of all four filter dropdowns");
        boolean allDisplayed = catalogPage.areAllFiltersDisplayed();
        Assert.assertTrue(allDisplayed,
                "All four filter dropdowns (Country, Package Type, Group, Language) should be displayed");
        logPass("All four filter dropdowns are visible on the page");
    }

    // =========================================================
    //  TC_003 - NOTE Message
    // =========================================================

    @Test(description = "TC_003 - Verify NOTE message about latest version is displayed",
          groups = {"regression"})
    public void verifyNoteMessageDisplayed() {
        logStep("Checking for NOTE message about latest version of products");
        boolean noteDisplayed = catalogPage.isNoteMessageDisplayed();
        Assert.assertTrue(noteDisplayed,
                "NOTE message about latest version supply should be displayed");
        logPass("NOTE message is displayed as expected");
    }

    // =========================================================
    //  TC_004 - Country Dropdown Options
    // =========================================================

    @Test(description = "TC_004 - Verify Country dropdown contains multiple countries including United States",
          groups = {"regression"})
    public void verifyCountryDropdownOptions() {
        logStep("Fetching all country options from dropdown");
        List<String> countries = catalogPage.getAllCountryOptions();
        logStep("Total country options found: " + countries.size());

        Assert.assertTrue(countries.size() > 10,
                "Country dropdown should have more than 10 options. Found: " + countries.size());
        Assert.assertTrue(countries.contains("United States"),
                "Country dropdown should contain 'United States'");

        logPass("Country dropdown contains " + countries.size()
                + " options and includes 'United States'");
    }

    // =========================================================
    //  TC_005 - All 6 Package Type Options Present
    // =========================================================

    @Test(description = "TC_005 - Verify all 6 Package Type options are present as per IBM ShopZ doc",
          groups = {"regression"})
    public void verifyPackageTypeOptions() {
        logStep("Fetching all Package Type options");
        List<String> packageTypes = catalogPage.getAllPackageTypeOptions();
        logStep("Package types found: " + packageTypes);

        // Expected options from the IBM ShopZ doc screenshots
        String[] expectedTypes = {
            "Linux on z-Standalone products and fixes",
            "z/OS-z/OSMF Portable Software Instance (ServerPac - system, subsystem or products)",
            "z/OS-CBPDO (products)",
            "z/OS-Open Source SW on z/OS (CBPDO)",
            "z/VM-VM SDO version 7",
            "z/VSE-VSE SIPO version 6"
        };

        for (String expectedType : expectedTypes) {
            boolean found = packageTypes.stream()
                    .anyMatch(pt -> pt.contains(expectedType) || expectedType.contains(pt.trim()));
            logStep("Checking for package type: " + expectedType + " -> Found: " + found);
            Assert.assertTrue(found,
                    "Package type not found in dropdown: " + expectedType);
        }

        logPass("All 6 Package Type options verified successfully");
    }

    // =========================================================
    //  TC_006 - Group Dropdown
    // =========================================================

    @Test(description = "TC_006 - Verify Group dropdown is displayed and has options",
          groups = {"regression"})
    public void verifyGroupDropdown() {
        logStep("Fetching all Group options");
        List<String> groups = catalogPage.getAllGroupOptions();
        logStep("Group options count: " + groups.size());
        Assert.assertTrue(groups.size() >= 1,
                "Group dropdown should have at least 1 option. Found: " + groups.size());
        logPass("Group dropdown is present with " + groups.size() + " options");
    }

    // =========================================================
    //  TC_007 - Language Dropdown
    // =========================================================

    @Test(description = "TC_007 - Verify Language dropdown has options",
          groups = {"regression"})
    public void verifyLanguageDropdown() {
        logStep("Fetching all Language options");
        List<String> languages = catalogPage.getAllLanguageOptions();
        logStep("Language options count: " + languages.size());
        Assert.assertTrue(languages.size() >= 1,
                "Language dropdown should have at least 1 option. Found: " + languages.size());
        logPass("Language dropdown is present with " + languages.size() + " options");
    }

    // =========================================================
    //  TC_008 - Select Country United States
    // =========================================================

    @Test(description = "TC_008 - Select 'United States' from Country dropdown and verify",
          groups = {"regression"})
    public void selectCountryUnitedStates() {
        logStep("Selecting 'United States' from Country dropdown");
        catalogPage.selectCountry("United States");
        String selected = catalogPage.getSelectedCountry();
        logStep("Selected country: " + selected);
        Assert.assertEquals(selected, "United States",
                "Selected country should be 'United States'");
        logPass("Country 'United States' selected and verified");
    }

    // =========================================================
    //  TC_009 - Search with z/OS-CBPDO package type
    // =========================================================

    @Test(description = "TC_009 - Select country + z/OS-CBPDO package type and click Find",
          groups = {"regression", "functional"})
    public void searchWithZosCbpdoPackageType() {
        logStep("Selecting Country: United States");
        catalogPage.selectCountry("United States");

        logStep("Selecting Package Type: z/OS-CBPDO (products)");
        catalogPage.selectPackageType("z/OS-CBPDO (products)");

        logStep("Clicking Find button");
        catalogPage.clickFind();

        String countText = catalogPage.getProductCountText();
        logStep("Product count label text: " + countText);
        logPass("Search completed with z/OS-CBPDO package type. Count: " + countText);
    }

    // =========================================================
    //  TC_010 - Search with Linux package type
    // =========================================================

    @Test(description = "TC_010 - Select Linux on z-Standalone products and click Find",
          groups = {"regression", "functional"})
    public void searchWithLinuxPackageType() {
        logStep("Selecting Country: United States");
        catalogPage.selectCountry("United States");

        logStep("Selecting Package Type: Linux on z-Standalone products and fixes");
        catalogPage.selectPackageType("Linux on z-Standalone products and fixes");

        logStep("Clicking Find button");
        catalogPage.clickFind();

        String countText = catalogPage.getProductCountText();
        logStep("Product count text: " + countText);
        logPass("Search completed with Linux package type. Count: " + countText);
    }

    // =========================================================
    //  TC_011 - Search with z/VM package type
    // =========================================================

    @Test(description = "TC_011 - Select z/VM-VM SDO version 7 package type and click Find",
          groups = {"regression", "functional"})
    public void searchWithZvmPackageType() {
        logStep("Selecting Country: United States");
        catalogPage.selectCountry("United States");

        logStep("Selecting Package Type: z/VM-VM SDO version 7");
        catalogPage.selectPackageType("z/VM-VM SDO version 7");

        logStep("Clicking Find button");
        catalogPage.clickFind();

        logPass("Search completed with z/VM package type");
    }

    // =========================================================
    //  TC_012 - Search with z/VSE package type
    // =========================================================

    @Test(description = "TC_012 - Select z/VSE-VSE SIPO version 6 package type and click Find",
          groups = {"regression", "functional"})
    public void searchWithZvsePackageType() {
        logStep("Selecting Country: United States");
        catalogPage.selectCountry("United States");

        logStep("Selecting Package Type: z/VSE-VSE SIPO version 6");
        catalogPage.selectPackageType("z/VSE-VSE SIPO version 6");

        logStep("Clicking Find button");
        catalogPage.clickFind();

        logPass("Search completed with z/VSE package type");
    }

    // =========================================================
    //  TC_013 - Product Count Label After Search
    // =========================================================

    @Test(description = "TC_013 - Verify product count label is displayed after search",
          groups = {"regression"})
    public void verifyProductCountAfterSearch() {
        logStep("Performing search with United States + z/OS-CBPDO");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();

        String countText = catalogPage.getProductCountText();
        logStep("Product count text: " + countText);
        Assert.assertFalse(countText.isEmpty(),
                "Product count label should be displayed after search");
        logPass("Product count label displayed: " + countText);
    }

    // =========================================================
    //  TC_014 - Results Table Appears After Search
    // =========================================================

    @Test(description = "TC_014 - Verify results table appears after valid catalog search",
          groups = {"regression", "functional"})
    public void verifyResultsTableAfterSearch() {
        logStep("Performing search with United States + z/OS-CBPDO");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();

        boolean tableDisplayed = catalogPage.isResultsTableDisplayed();
        logStep("Results table displayed: " + tableDisplayed);
        Assert.assertTrue(tableDisplayed, "Results table should be displayed after search");
        logPass("Results table is displayed after search");
    }

    // =========================================================
    //  TC_015 - FMID Links Present After Search
    // =========================================================

    @Test(description = "TC_015 - Verify FMID links are present after a valid search",
          groups = {"regression", "functional"})
    public void verifyFmidLinksAfterSearch() {
        logStep("Performing search with United States + z/OS-CBPDO");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();

        int fmidCount = catalogPage.getFmidLinks().size();
        logStep("FMID links found: " + fmidCount);
        Assert.assertTrue(fmidCount > 0,
                "FMID links should be present in results. Found: " + fmidCount);
        logPass("FMID links verified. Count: " + fmidCount);
    }
}
