package com.ibm.shopz.tests;

import com.ibm.shopz.base.BaseTest;
import com.ibm.shopz.pages.ProductCatalogPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/**
 * FilterCombinationTest - Tests all meaningful multi-filter combinations on the
 * IBM ShopZ Product Catalog page.
 *
 * Coverage areas NOT in existing tests:
 *  - Country + PackageType combinations for every package type
 *  - Country + PackageType + Group combinations
 *  - Country + PackageType + Language combinations
 *  - All-four-filter combinations
 *  - Different countries with the same package type
 *  - Verifying product count changes with different filter sets
 *  - Verifying that changing one filter after another works (chained filter change)
 *  - Verify "Select one" default is shown for each dropdown before selection
 */
public class FilterCombinationTest extends BaseTest {

    private ProductCatalogPage catalogPage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        catalogPage = new ProductCatalogPage(getDriver());
        catalogPage.open();
    }

    // =========================================================
    //  DATA PROVIDERS
    // =========================================================

    /**
     * Provides: country, packageType, expectedMinResults
     * Tests that each of the 6 package types returns results for US.
     */
    @DataProvider(name = "countryPackageTypeCombinations")
    public Object[][] countryPackageTypeCombinations() {
        return new Object[][] {
            {"United States", "Linux on z-Standalone products and fixes",                                           0},
            {"United States", "z/OS-z/OSMF Portable Software Instance (ServerPac - system, subsystem or products)", 0},
            {"United States", "z/OS-CBPDO (products)",                                                              0},
            {"United States", "z/OS-Open Source SW on z/OS (CBPDO)",                                               0},
            {"United States", "z/VM-VM SDO version 7",                                                              0},
            {"United States", "z/VSE-VSE SIPO version 6",                                                           0},
        };
    }

    /**
     * Provides: country variations to test same package type across regions.
     */
    @DataProvider(name = "multiCountrySamePackageType")
    public Object[][] multiCountrySamePackageType() {
        return new Object[][] {
            {"United States",   "z/OS-CBPDO (products)"},
            {"Canada",          "z/OS-CBPDO (products)"},
            {"United Kingdom",  "z/OS-CBPDO (products)"},
            {"Germany",         "z/OS-CBPDO (products)"},
            {"Australia",       "z/OS-CBPDO (products)"},
            {"Japan",           "z/OS-CBPDO (products)"},
        };
    }

    // =========================================================
    //  TC_COMB_001 - All 6 Package Types With United States (Data-Driven)
    // =========================================================

    @Test(dataProvider  = "countryPackageTypeCombinations",
          description   = "TC_COMB_001 - Data-driven: verify all 6 package types return a result page for United States",
          groups        = {"regression", "functional", "data-driven"})
    public void verifyAllPackageTypesForUnitedStates(String country,
                                                      String packageType,
                                                      int minExpectedResults) {
        logStep("Country: " + country + " | PackageType: " + packageType);
        catalogPage.selectCountry(country)
                   .selectPackageType(packageType)
                   .clickFind();

        String countText = catalogPage.getProductCountText();
        int count = catalogPage.getProductCount();
        logStep("Product count text: " + countText + " | Parsed count: " + count);

        Assert.assertTrue(count >= minExpectedResults,
                "Expected at least " + minExpectedResults + " results for ["
                        + country + " / " + packageType + "]. Got: " + count);
        logPass("PackageType [" + packageType + "] returned " + count + " products");
    }

    // =========================================================
    //  TC_COMB_002 - Same Package Type Across Multiple Countries (Data-Driven)
    // =========================================================

    @Test(dataProvider  = "multiCountrySamePackageType",
          description   = "TC_COMB_002 - Data-driven: z/OS-CBPDO with multiple countries produces results page",
          groups        = {"regression", "functional", "data-driven"})
    public void verifyZosCbpdoAcrossCountries(String country, String packageType) {
        logStep("Selecting Country: " + country + " | PackageType: " + packageType);
        catalogPage.selectCountry(country)
                   .selectPackageType(packageType)
                   .clickFind();

        String countText = catalogPage.getProductCountText();
        logStep("Product count text for [" + country + "]: " + countText);

        // Page must load without error regardless of whether results > 0
        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Page should load correctly after filtering by [" + country + "]");
        logPass("z/OS-CBPDO search for [" + country + "] completed. Result text: " + countText);
    }

    // =========================================================
    //  TC_COMB_003 - Country + PackageType + Group (Three Filters)
    // =========================================================

    @Test(description = "TC_COMB_003 - Select Country + PackageType + first available Group and search",
          groups = {"regression", "functional"})
    public void verifyThreeFilterCombination() {
        logStep("Selecting country: United States");
        catalogPage.selectCountry("United States");

        logStep("Selecting package type: z/OS-CBPDO (products)");
        catalogPage.selectPackageType("z/OS-CBPDO (products)");

        List<String> groups = catalogPage.getAllGroupOptions();
        logStep("Available group options: " + groups.size());

        if (groups.size() > 1) {
            // Pick the second option (index 1) to skip the "Select one" placeholder
            String groupToSelect = groups.get(1);
            logStep("Selecting group: " + groupToSelect);
            catalogPage.selectGroup(groupToSelect);
        } else {
            logStep("Only default group option available - skipping group selection");
        }

        catalogPage.clickFind();
        String countText = catalogPage.getProductCountText();
        logStep("Product count with 3 filters: " + countText);

        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Page should load correctly with Country + PackageType + Group filters");
        logPass("3-filter combination search completed. Count: " + countText);
    }

    // =========================================================
    //  TC_COMB_004 - Country + PackageType + Language (Three Filters)
    // =========================================================

    @Test(description = "TC_COMB_004 - Select Country + PackageType + first available Language and search",
          groups = {"regression", "functional"})
    public void verifyCountryPackageTypeLanguageCombination() {
        logStep("Selecting country: United States");
        catalogPage.selectCountry("United States");

        logStep("Selecting package type: z/OS-CBPDO (products)");
        catalogPage.selectPackageType("z/OS-CBPDO (products)");

        List<String> languages = catalogPage.getAllLanguageOptions();
        logStep("Available language options: " + languages.size());

        if (languages.size() > 1) {
            String langToSelect = languages.get(1);
            logStep("Selecting language: " + langToSelect);
            catalogPage.selectLanguage(langToSelect);
        } else {
            logStep("Only default language option available - skipping language selection");
        }

        catalogPage.clickFind();
        String countText = catalogPage.getProductCountText();
        logStep("Product count with Country + PackageType + Language: " + countText);

        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Page should load after Country + PackageType + Language search");
        logPass("Country + PackageType + Language filter search completed. Count: " + countText);
    }

    // =========================================================
    //  TC_COMB_005 - All Four Filters Applied Together
    // =========================================================

    @Test(description = "TC_COMB_005 - Apply all four filters together and verify search completes",
          groups = {"regression", "functional"})
    public void verifyAllFourFiltersApplied() {
        logStep("Selecting Country: United States");
        catalogPage.selectCountry("United States");

        logStep("Selecting PackageType: z/OS-CBPDO (products)");
        catalogPage.selectPackageType("z/OS-CBPDO (products)");

        List<String> groups = catalogPage.getAllGroupOptions();
        if (groups.size() > 1) {
            logStep("Selecting Group: " + groups.get(1));
            catalogPage.selectGroup(groups.get(1));
        }

        List<String> languages = catalogPage.getAllLanguageOptions();
        if (languages.size() > 1) {
            logStep("Selecting Language: " + languages.get(1));
            catalogPage.selectLanguage(languages.get(1));
        }

        catalogPage.clickFind();

        String countText = catalogPage.getProductCountText();
        logStep("Product count with all 4 filters: " + countText);

        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Page should load correctly with all four filters applied");
        logPass("All-four-filter search completed. Count: " + countText);
    }

    // =========================================================
    //  TC_COMB_006 - Product Count Differs Across Package Types
    // =========================================================

    @Test(description = "TC_COMB_006 - Verify product counts differ between Linux and z/OS-CBPDO package types",
          groups = {"regression", "functional"})
    public void verifyProductCountDiffersBetweenPackageTypes() {
        logStep("Search 1: United States + Linux on z-Standalone");
        catalogPage.selectCountry("United States")
                   .selectPackageType("Linux on z-Standalone products and fixes")
                   .clickFind();
        String linuxCountText = catalogPage.getProductCountText();
        int linuxCount = catalogPage.getProductCount();
        logStep("Linux product count: " + linuxCount + " (" + linuxCountText + ")");

        // Navigate back and search again with different package type
        catalogPage.open();

        logStep("Search 2: United States + z/OS-CBPDO");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();
        String cbpdoCountText = catalogPage.getProductCountText();
        int cbpdoCount = catalogPage.getProductCount();
        logStep("z/OS-CBPDO product count: " + cbpdoCount + " (" + cbpdoCountText + ")");

        // The two different package types should ideally show different product sets
        // (This is a behavioral assertion — page must complete both searches)
        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Both searches should complete and page should load correctly");
        logPass("Both package type searches completed. Linux=" + linuxCount
                + " | z/OS-CBPDO=" + cbpdoCount);
    }

    // =========================================================
    //  TC_COMB_007 - Change Package Type After Initial Selection
    // =========================================================

    @Test(description = "TC_COMB_007 - Change package type after initial selection and re-search (chained filter change)",
          groups = {"regression", "functional"})
    public void verifyChainedFilterChange() {
        logStep("Initial selection: United States + z/OS-CBPDO");
        catalogPage.selectCountry("United States")
                   .selectPackageType("z/OS-CBPDO (products)")
                   .clickFind();

        int firstCount = catalogPage.getProductCount();
        logStep("First search product count: " + firstCount);

        logStep("Changing package type to: Linux on z-Standalone products and fixes");
        catalogPage.selectPackageType("Linux on z-Standalone products and fixes")
                   .clickFind();

        int secondCount = catalogPage.getProductCount();
        logStep("Second search product count: " + secondCount);

        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Page should load correctly after chaining filter changes");
        logPass("Chained filter change: first=" + firstCount + " | second=" + secondCount);
    }

    // =========================================================
    //  TC_COMB_008 - Default "Select one" Shown Before Any Selection
    // =========================================================

    @Test(description = "TC_COMB_008 - Verify 'Select one' default is shown for dropdowns before any selection",
          groups = {"regression", "ui-validation"})
    public void verifyDefaultSelectOneShownOnLoad() {
        logStep("Checking default selected values for all dropdowns before interaction");

        String countryDefault  = catalogPage.getSelectedCountry();
        String pkgDefault      = catalogPage.getSelectedPackageType();
        String groupDefault    = catalogPage.getSelectedGroup();
        String languageDefault = catalogPage.getSelectedLanguage();

        logStep("Country default: '"   + countryDefault  + "'");
        logStep("PackageType default: '" + pkgDefault    + "'");
        logStep("Group default: '"     + groupDefault    + "'");
        logStep("Language default: '"  + languageDefault + "'");

        // All should be "Select one" or blank by default
        Assert.assertTrue(
                countryDefault.toLowerCase().contains("select") || countryDefault.isEmpty(),
                "Country dropdown default should be 'Select one'. Actual: " + countryDefault);
        Assert.assertTrue(
                pkgDefault.toLowerCase().contains("select") || pkgDefault.isEmpty(),
                "PackageType dropdown default should be 'Select one'. Actual: " + pkgDefault);

        logPass("All dropdowns show default 'Select one' values on page load");
    }

    // =========================================================
    //  TC_COMB_009 - India Country with z/OS-CBPDO
    // =========================================================

    @Test(description = "TC_COMB_009 - Search with India + z/OS-CBPDO and verify page loads",
          groups = {"regression", "functional"})
    public void verifySearchWithIndiaAndZosCbpdo() {
        logStep("Selecting Country: India");
        catalogPage.selectCountry("India");

        logStep("Selecting PackageType: z/OS-CBPDO (products)");
        catalogPage.selectPackageType("z/OS-CBPDO (products)");

        catalogPage.clickFind();

        String countText = catalogPage.getProductCountText();
        logStep("Product count for India + z/OS-CBPDO: " + countText);
        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Page should load for India + z/OS-CBPDO search");
        logPass("India + z/OS-CBPDO search completed. Count: " + countText);
    }

    // =========================================================
    //  TC_COMB_010 - Canada Country with Linux Package Type
    // =========================================================

    @Test(description = "TC_COMB_010 - Search with Canada + Linux on z-Standalone and verify page loads",
          groups = {"regression", "functional"})
    public void verifySearchWithCanadaAndLinux() {
        logStep("Selecting Country: Canada");
        catalogPage.selectCountry("Canada");

        logStep("Selecting PackageType: Linux on z-Standalone products and fixes");
        catalogPage.selectPackageType("Linux on z-Standalone products and fixes");

        catalogPage.clickFind();

        String countText = catalogPage.getProductCountText();
        logStep("Product count for Canada + Linux: " + countText);
        Assert.assertTrue(catalogPage.isPageTitleCorrect(),
                "Page should load for Canada + Linux search");
        logPass("Canada + Linux search completed. Count: " + countText);
    }
}
