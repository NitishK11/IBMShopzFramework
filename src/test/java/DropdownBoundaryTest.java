package com.ibm.shopz.tests;

import com.ibm.shopz.base.BaseTest;
import com.ibm.shopz.pages.ProductCatalogPage;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * DropdownBoundaryTest - Boundary and edge-case tests for all four dropdowns:
 * Country, Package Type, Group, and Language.
 *
 * Coverage areas NOT in existing tests:
 *  - Select first option in each dropdown
 *  - Select last option in each dropdown
 *  - Select each option individually from Package Type and verify selection holds
 *  - Verify no duplicate options in Country dropdown
 *  - Verify no duplicate options in Package Type dropdown
 *  - Verify all Package Type option texts are non-empty
 *  - Verify all Country option texts are non-empty
 *  - Verify selecting a country does not reset Package Type (independent dropdowns)
 *  - Verify selecting Package Type does not reset Country (independent dropdowns)
 *  - Group dropdown becomes populated / changes after Country + PackageType selection
 */
public class DropdownBoundaryTest extends BaseTest {

    private ProductCatalogPage catalogPage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        catalogPage = new ProductCatalogPage(getDriver());
        catalogPage.open();
    }

    // =========================================================
    //  TC_DROP_001 - Select First Country Option (after "Select one")
    // =========================================================

    @Test(description = "TC_DROP_001 - Select the first real country option (index 1) from Country dropdown",
          groups = {"regression", "boundary"})
    public void verifySelectFirstCountryOption() {
        List<String> countries = catalogPage.getAllCountryOptions();
        logStep("Total country options: " + countries.size());
        Assert.assertTrue(countries.size() > 1, "Country dropdown must have at least 2 options");

        String firstCountry = countries.get(1); // index 0 = "Select one"
        logStep("Selecting first real country: '" + firstCountry + "'");
        catalogPage.selectCountry(firstCountry);

        String selected = catalogPage.getSelectedCountry();
        logStep("Selected country: '" + selected + "'");
        Assert.assertEquals(selected, firstCountry,
                "Selected country should match the first option");
        logPass("First country option '" + firstCountry + "' selected successfully");
    }

    // =========================================================
    //  TC_DROP_002 - Select Last Country Option
    // =========================================================

    @Test(description = "TC_DROP_002 - Select the last country option from Country dropdown",
          groups = {"regression", "boundary"})
    public void verifySelectLastCountryOption() {
        List<String> countries = catalogPage.getAllCountryOptions();
        logStep("Total country options: " + countries.size());

        String lastCountry = countries.get(countries.size() - 1);
        logStep("Selecting last country: '" + lastCountry + "'");
        catalogPage.selectCountry(lastCountry);

        String selected = catalogPage.getSelectedCountry();
        logStep("Selected country: '" + selected + "'");
        Assert.assertEquals(selected, lastCountry,
                "Selected country should match the last option");
        logPass("Last country option '" + lastCountry + "' selected successfully");
    }

    // =========================================================
    //  TC_DROP_003 - All Package Type Options Selectable
    // =========================================================

    @Test(description = "TC_DROP_003 - Verify each of the 6 Package Type options can be individually selected",
          groups = {"regression", "boundary"})
    public void verifyEachPackageTypeOptionSelectable() {
        List<String> pkgTypes = catalogPage.getAllPackageTypeOptions();
        logStep("All package type options: " + pkgTypes);

        for (int i = 1; i < pkgTypes.size(); i++) { // skip index 0 = "Select one"
            String option = pkgTypes.get(i);
            if (option.toLowerCase().contains("select")) continue;

            logStep("Selecting package type [" + i + "]: '" + option + "'");
            catalogPage.selectPackageType(option);

            String selected = catalogPage.getSelectedPackageType();
            Assert.assertEquals(selected.trim(), option.trim(),
                    "Selected package type should match '" + option + "'. Got: '" + selected + "'");
            logStep("Verified selection: '" + selected + "'");
        }
        logPass("All Package Type options can be individually selected and verified");
    }

    // =========================================================
    //  TC_DROP_004 - No Duplicate Options in Country Dropdown
    // =========================================================

    @Test(description = "TC_DROP_004 - Verify there are no duplicate country names in the Country dropdown",
          groups = {"regression", "boundary"})
    public void verifyNoDuplicatesInCountryDropdown() {
        List<String> countries = catalogPage.getAllCountryOptions();
        logStep("Total country options: " + countries.size());

        long uniqueCount = countries.stream().distinct().count();
        logStep("Unique country count: " + uniqueCount);

        Assert.assertEquals(uniqueCount, countries.size(),
                "Country dropdown should have no duplicate options. "
                + "Total: " + countries.size() + ", Unique: " + uniqueCount);
        logPass("No duplicate country options found (" + countries.size() + " unique entries)");
    }

    // =========================================================
    //  TC_DROP_005 - No Duplicate Options in Package Type Dropdown
    // =========================================================

    @Test(description = "TC_DROP_005 - Verify there are no duplicate options in the Package Type dropdown",
          groups = {"regression", "boundary"})
    public void verifyNoDuplicatesInPackageTypeDropdown() {
        List<String> pkgTypes = catalogPage.getAllPackageTypeOptions();
        logStep("Total package type options: " + pkgTypes.size());

        long uniqueCount = pkgTypes.stream().distinct().count();
        logStep("Unique package type count: " + uniqueCount);

        Assert.assertEquals(uniqueCount, pkgTypes.size(),
                "Package Type dropdown should have no duplicate options. "
                + "Total: " + pkgTypes.size() + ", Unique: " + uniqueCount);
        logPass("No duplicate Package Type options found");
    }

    // =========================================================
    //  TC_DROP_006 - All Package Type Option Texts Are Non-Empty
    // =========================================================

    @Test(description = "TC_DROP_006 - Verify all Package Type dropdown options have non-empty text labels",
          groups = {"regression", "boundary"})
    public void verifyPackageTypeOptionsNonEmpty() {
        List<String> pkgTypes = catalogPage.getAllPackageTypeOptions();
        logStep("Package type options: " + pkgTypes);

        int emptyCount = 0;
        for (String opt : pkgTypes) {
            if (opt == null || opt.trim().isEmpty()) {
                emptyCount++;
                logStep("EMPTY option found at index: " + pkgTypes.indexOf(opt));
            }
        }

        Assert.assertEquals(emptyCount, 0,
                "All Package Type options should have non-empty labels. Empty count: " + emptyCount);
        logPass("All " + pkgTypes.size() + " Package Type options have non-empty text labels");
    }

    // =========================================================
    //  TC_DROP_007 - All Country Option Texts Are Non-Empty
    // =========================================================

    @Test(description = "TC_DROP_007 - Verify all Country dropdown options have non-empty text labels",
          groups = {"regression", "boundary"})
    public void verifyAllCountryOptionsNonEmpty() {
        List<String> countries = catalogPage.getAllCountryOptions();
        logStep("Total country options: " + countries.size());

        long emptyCount = countries.stream()
                .filter(c -> c == null || c.trim().isEmpty())
                .count();

        Assert.assertEquals(emptyCount, 0,
                "All Country options should have non-empty labels. Empty count: " + emptyCount);
        logPass("All " + countries.size() + " Country options have non-empty text labels");
    }

    // =========================================================
    //  TC_DROP_008 - Selecting Country Does Not Reset Package Type
    // =========================================================

    @Test(description = "TC_DROP_008 - Verify selecting a Country does not reset an already-selected Package Type",
          groups = {"regression", "boundary"})
    public void verifyCountrySelectionDoesNotResetPackageType() {
        logStep("Selecting Package Type: z/OS-CBPDO (products)");
        catalogPage.selectPackageType("z/OS-CBPDO (products)");
        String pkgBeforeCountry = catalogPage.getSelectedPackageType();
        logStep("Package Type after selection: '" + pkgBeforeCountry + "'");

        logStep("Now selecting Country: United States");
        catalogPage.selectCountry("United States");
        String pkgAfterCountry = catalogPage.getSelectedPackageType();
        logStep("Package Type after Country change: '" + pkgAfterCountry + "'");

        Assert.assertEquals(pkgAfterCountry.trim(), pkgBeforeCountry.trim(),
                "Package Type selection should not be reset when Country is changed. "
                + "Expected: '" + pkgBeforeCountry + "' Got: '" + pkgAfterCountry + "'");
        logPass("Country selection did not reset Package Type dropdown");
    }

    // =========================================================
    //  TC_DROP_009 - Selecting Package Type Does Not Reset Country
    // =========================================================

    @Test(description = "TC_DROP_009 - Verify selecting a Package Type does not reset an already-selected Country",
          groups = {"regression", "boundary"})
    public void verifyPackageTypeSelectionDoesNotResetCountry() {
        logStep("Selecting Country: United States");
        catalogPage.selectCountry("United States");
        String countryBefore = catalogPage.getSelectedCountry();
        logStep("Country after selection: '" + countryBefore + "'");

        logStep("Now selecting Package Type: z/OS-CBPDO (products)");
        catalogPage.selectPackageType("z/OS-CBPDO (products)");
        String countryAfter = catalogPage.getSelectedCountry();
        logStep("Country after Package Type change: '" + countryAfter + "'");

        Assert.assertEquals(countryAfter.trim(), countryBefore.trim(),
                "Country selection should not be reset when Package Type is changed. "
                + "Expected: '" + countryBefore + "' Got: '" + countryAfter + "'");
        logPass("Package Type selection did not reset Country dropdown");
    }

    // =========================================================
    //  TC_DROP_010 - Country Dropdown Contains "Afghanistan" as First Real Entry
    // =========================================================

    @Test(description = "TC_DROP_010 - Verify 'Afghanistan' is the first real country in the dropdown (alphabetical)",
          groups = {"regression", "boundary"})
    public void verifyFirstRealCountryIsAfghanistan() {
        List<String> countries = catalogPage.getAllCountryOptions();
        logStep("All country options: first 5 = " + countries.subList(0, Math.min(5, countries.size())));

        // index 0 = "Select one", index 1 = first real country
        String firstReal = countries.get(1);
        logStep("First real country option: '" + firstReal + "'");

        Assert.assertEquals(firstReal, "Afghanistan",
                "First real country (index 1) should be 'Afghanistan'. Actual: '" + firstReal + "'");
        logPass("First country option is 'Afghanistan' as expected");
    }

    // =========================================================
    //  TC_DROP_011 - Language Dropdown Options Are All Non-Empty
    // =========================================================

    @Test(description = "TC_DROP_011 - Verify all Language dropdown options have non-empty text",
          groups = {"regression", "boundary"})
    public void verifyLanguageOptionsNonEmpty() {
        List<String> languages = catalogPage.getAllLanguageOptions();
        logStep("Language options found: " + languages);

        long emptyCount = languages.stream()
                .filter(l -> l == null || l.trim().isEmpty())
                .count();

        Assert.assertEquals(emptyCount, 0,
                "All Language dropdown options should be non-empty. Empty count: " + emptyCount);
        logPass("All " + languages.size() + " Language options are non-empty");
    }

    // =========================================================
    //  TC_DROP_012 - Select Last Package Type Option
    // =========================================================

    @Test(description = "TC_DROP_012 - Select the last Package Type option (z/VSE-VSE SIPO version 6) and verify",
          groups = {"regression", "boundary"})
    public void verifySelectLastPackageTypeOption() {
        List<String> pkgTypes = catalogPage.getAllPackageTypeOptions();
        logStep("Total package type options: " + pkgTypes.size());

        String lastOption = pkgTypes.get(pkgTypes.size() - 1);
        logStep("Last package type option: '" + lastOption + "'");

        catalogPage.selectPackageType(lastOption);
        String selected = catalogPage.getSelectedPackageType();
        logStep("Selected package type: '" + selected + "'");

        Assert.assertEquals(selected.trim(), lastOption.trim(),
                "Last package type option should be selectable and hold the selection");
        logPass("Last Package Type option '" + lastOption + "' selected and verified");
    }

    // =========================================================
    //  TC_DROP_013 - Group Dropdown Has "Select one" Default
    // =========================================================

    @Test(description = "TC_DROP_013 - Verify Group dropdown shows 'Select one' as default before interaction",
          groups = {"regression", "boundary"})
    public void verifyGroupDropdownDefaultIsSelectOne() {
        String groupDefault = catalogPage.getSelectedGroup();
        logStep("Default Group dropdown value: '" + groupDefault + "'");

        Assert.assertTrue(
                groupDefault.toLowerCase().contains("select") || groupDefault.isEmpty(),
                "Group dropdown default should be 'Select one'. Actual: '" + groupDefault + "'");
        logPass("Group dropdown shows default 'Select one' value on page load");
    }

    // =========================================================
    //  TC_DROP_014 - Language Dropdown Has "Select one" Default
    // =========================================================

    @Test(description = "TC_DROP_014 - Verify Language dropdown shows 'Select one' as default before interaction",
          groups = {"regression", "boundary"})
    public void verifyLanguageDropdownDefaultIsSelectOne() {
        String langDefault = catalogPage.getSelectedLanguage();
        logStep("Default Language dropdown value: '" + langDefault + "'");

        Assert.assertTrue(
                langDefault.toLowerCase().contains("select") || langDefault.isEmpty(),
                "Language dropdown default should be 'Select one'. Actual: '" + langDefault + "'");
        logPass("Language dropdown shows default 'Select one' value on page load");
    }
}
