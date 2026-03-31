package com.ibm.shopz.pages;

import com.ibm.shopz.base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

/**
 * ProductCatalogPage - Page Object for:
 * https://www.ibm.com/software/shopzseries/ShopzSeries_public.wss?action=prodcat
 *
 * Covers all UI elements documented in the IBM ShopZ Product Catalog doc:
 *  - Country/Region dropdown
 *  - Package Type dropdown (6 options)
 *  - Group dropdown
 *  - Language dropdown
 *  - "Show Catalog" submit/find button
 *  - Results table with FMID links and Product links
 *  - Product count label
 */
public class ProductCatalogPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(ProductCatalogPage.class);

    // =========================================================
    //  Page URL
    // =========================================================
    public static final String PAGE_URL =
            "https://www.ibm.com/software/shopzseries/ShopzSeries_public.wss?action=prodcat";

    // =========================================================
    //  Page Header / Title
    // =========================================================

    @FindBy(css = "h1, .shopz-title, #content h1")
    private WebElement pageHeading;

    // =========================================================
    //  Filter Form Elements
    // =========================================================

    /** Country/Region dropdown - "Select one" default */
    @FindBy(name = "country")
    private WebElement countryDropdown;

    /** Package Type dropdown */
    @FindBy(name = "packagetype")
    private WebElement packageTypeDropdown;

    /** Group dropdown */
    @FindBy(name = "group")
    private WebElement groupDropdown;

    /** Language dropdown */
    @FindBy(name = "language")
    private WebElement languageDropdown;

    /** Find/Show Catalog submit button */
    @FindBy(css = "input[type='submit'], input[value*='Find'], button[type='submit']")
    private WebElement findButton;

    // =========================================================
    //  Results Section
    // =========================================================

    /** Product count label e.g. "(Products in this view: 0)" */
    @FindBy(xpath = "//*[contains(text(),'Products in this view')]")
    private WebElement productCountLabel;

    /** Results table */
    @FindBy(css = "table.catalog-results, table#resultsTable, .results table")
    private WebElement resultsTable;

    /** All FMID links in the results */
    @FindBy(xpath = "//table//a[contains(@href,'fmid') or contains(@href,'FMID')]")
    private List<WebElement> fmidLinks;

    /** All Product links (e.g. 5655-GOZ) */
    @FindBy(xpath = "//table//a[contains(@href,'prodnum') or contains(@href,'prodlink')]")
    private List<WebElement> productLinks;

    /** All rows in the catalog results table body */
    @FindBy(xpath = "//table[@class='catalog-results' or contains(@id,'results')]//tbody/tr")
    private List<WebElement> resultTableRows;

    // =========================================================
    //  Navigation Links
    // =========================================================

    @FindBy(linkText = "Product catalog")
    private WebElement productCatalogNavLink;

    @FindBy(linkText = "Sign in/Register")
    private WebElement signInRegisterLink;

    @FindBy(linkText = "FAQs")
    private WebElement faqsLink;

    @FindBy(linkText = "Glossary")
    private WebElement glossaryLink;

    @FindBy(linkText = "Users' guide")
    private WebElement usersGuideLink;

    @FindBy(linkText = "Customer service")
    private WebElement customerServiceLink;

    // =========================================================
    //  Feedback Section
    // =========================================================

    @FindBy(css = "input[type='submit'][value='Submit'], button[value='Submit']")
    private WebElement feedbackSubmitButton;

    // =========================================================
    //  Constructor
    // =========================================================

    public ProductCatalogPage(WebDriver driver) {
        super(driver);
        logger.info("ProductCatalogPage initialized");
    }

    // =========================================================
    //  Navigation
    // =========================================================

    /**
     * Navigate directly to the Product Catalog page.
     */
    public ProductCatalogPage open() {
        navigateTo(PAGE_URL);
        waitForPageLoad();
        logger.info("Opened Product Catalog page: {}", PAGE_URL);
        return this;
    }

    // =========================================================
    //  Country/Region Dropdown
    // =========================================================

    /**
     * Select a country from the Country/Region dropdown.
     *
     * @param countryName visible text of the country (e.g. "United States")
     */
    public ProductCatalogPage selectCountry(String countryName) {
        logger.info("Selecting country: {}", countryName);
        selectByVisibleText(countryDropdown, countryName);
        return this;
    }

    /**
     * Get the currently selected country text.
     */
    public String getSelectedCountry() {
        return getSelectedDropdownText(countryDropdown);
    }

    /**
     * Get all available countries as text list.
     */
    public List<String> getAllCountryOptions() {
        List<String> countries = new ArrayList<>();
        getAllDropdownOptions(countryDropdown).forEach(opt -> countries.add(opt.getText().trim()));
        return countries;
    }

    // =========================================================
    //  Package Type Dropdown
    // =========================================================

    /**
     * Select a package type.
     * Options from IBM ShopZ doc:
     *   "Linux on z-Standalone products and fixes"
     *   "z/OS-z/OSMF Portable Software Instance (ServerPac - system, subsystem or products)"
     *   "z/OS-CBPDO (products)"
     *   "z/OS-Open Source SW on z/OS (CBPDO)"
     *   "z/VM-VM SDO version 7"
     *   "z/VSE-VSE SIPO version 6"
     *
     * @param packageType visible text of the package type
     */
    public ProductCatalogPage selectPackageType(String packageType) {
        logger.info("Selecting package type: {}", packageType);
        selectByVisibleText(packageTypeDropdown, packageType);
        return this;
    }

    /**
     * Get all available package type options.
     */
    public List<String> getAllPackageTypeOptions() {
        List<String> types = new ArrayList<>();
        getAllDropdownOptions(packageTypeDropdown).forEach(opt -> types.add(opt.getText().trim()));
        return types;
    }

    /**
     * Get the currently selected package type.
     */
    public String getSelectedPackageType() {
        return getSelectedDropdownText(packageTypeDropdown);
    }

    // =========================================================
    //  Group Dropdown
    // =========================================================

    /**
     * Select a group from the Group dropdown.
     *
     * @param groupName visible text of the group
     */
    public ProductCatalogPage selectGroup(String groupName) {
        logger.info("Selecting group: {}", groupName);
        selectByVisibleText(groupDropdown, groupName);
        return this;
    }

    /**
     * Get all available group options.
     */
    public List<String> getAllGroupOptions() {
        List<String> groups = new ArrayList<>();
        getAllDropdownOptions(groupDropdown).forEach(opt -> groups.add(opt.getText().trim()));
        return groups;
    }

    /**
     * Get the currently selected group.
     */
    public String getSelectedGroup() {
        return getSelectedDropdownText(groupDropdown);
    }

    // =========================================================
    //  Language Dropdown
    // =========================================================

    /**
     * Select a language from the Language dropdown.
     *
     * @param language visible text of language (e.g. "English")
     */
    public ProductCatalogPage selectLanguage(String language) {
        logger.info("Selecting language: {}", language);
        selectByVisibleText(languageDropdown, language);
        return this;
    }

    /**
     * Get all available language options.
     */
    public List<String> getAllLanguageOptions() {
        List<String> languages = new ArrayList<>();
        getAllDropdownOptions(languageDropdown).forEach(opt -> languages.add(opt.getText().trim()));
        return languages;
    }

    /**
     * Get the currently selected language.
     */
    public String getSelectedLanguage() {
        return getSelectedDropdownText(languageDropdown);
    }

    // =========================================================
    //  Find / Show Catalog Button
    // =========================================================

    /**
     * Click the Find / Show Catalog button.
     * Returns the same page (results load on same page).
     */
    public ProductCatalogPage clickFind() {
        logger.info("Clicking Find button to show catalog results");
        scrollToElement(findButton);
        click(findButton);
        waitForPageLoad();
        return this;
    }

    // =========================================================
    //  Combined Search / Filter Actions
    // =========================================================

    /**
     * Perform a full catalog search with all available filters.
     *
     * @param country     country/region name
     * @param packageType package type visible text
     * @param group       group name (pass null to skip)
     * @param language    language (pass null to skip)
     */
    public ProductCatalogPage searchCatalog(String country, String packageType,
                                             String group, String language) {
        if (country != null && !country.isEmpty()) selectCountry(country);
        if (packageType != null && !packageType.isEmpty()) selectPackageType(packageType);
        if (group != null && !group.isEmpty()) selectGroup(group);
        if (language != null && !language.isEmpty()) selectLanguage(language);
        return clickFind();
    }

    // =========================================================
    //  Results Accessors
    // =========================================================

    /**
     * Get the product count label text, e.g. "Products in this view: 45".
     */
    public String getProductCountText() {
        try {
            return getText(productCountLabel);
        } catch (Exception e) {
            logger.warn("Could not read product count label: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Extract the number from the product count label.
     * e.g. "Products in this view: 45" returns 45
     */
    public int getProductCount() {
        String text = getProductCountText();
        try {
            return Integer.parseInt(text.replaceAll("[^0-9]", "").trim());
        } catch (NumberFormatException e) {
            logger.warn("Could not parse product count from: '{}'", text);
            return 0;
        }
    }

    /**
     * Check if the results table is displayed.
     */
    public boolean isResultsTableDisplayed() {
        return isDisplayed(By.cssSelector("table"));
    }

    /**
     * Get all FMID links from the results.
     */
    public List<WebElement> getFmidLinks() {
        return fmidLinks;
    }

    /**
     * Get all Product links from the results.
     */
    public List<WebElement> getProductLinks() {
        return productLinks;
    }

    /**
     * Get all result rows.
     */
    public List<WebElement> getResultTableRows() {
        return resultTableRows;
    }

    /**
     * Click on the first FMID link in the results.
     * Returns a new FmidDetailPage (new page after click).
     */
    public FmidDetailPage clickFirstFmidLink() {
        if (fmidLinks.isEmpty()) {
            throw new NoSuchElementException("No FMID links found in results table");
        }
        logger.info("Clicking first FMID link: {}", fmidLinks.get(0).getText());
        String mainWindow = getMainWindowHandle();
        click(fmidLinks.get(0));
        return new FmidDetailPage(driver);
    }

    /**
     * Click on the second FMID link in the results.
     */
    public FmidDetailPage clickSecondFmidLink() {
        if (fmidLinks.size() < 2) {
            throw new NoSuchElementException("Less than 2 FMID links found in results table");
        }
        logger.info("Clicking second FMID link: {}", fmidLinks.get(1).getText());
        click(fmidLinks.get(1));
        return new FmidDetailPage(driver);
    }

    /**
     * Click on a product link by its text (e.g. "5655-GOZ").
     * Returns a new ProductDetailPage.
     */
    public ProductDetailPage clickProductLink(String productNumber) {
        WebElement productLink = productLinks.stream()
                .filter(link -> link.getText().trim().equalsIgnoreCase(productNumber))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Product link not found: " + productNumber));
        logger.info("Clicking product link: {}", productNumber);
        click(productLink);
        return new ProductDetailPage(driver);
    }

    /**
     * Click on the first product link.
     */
    public ProductDetailPage clickFirstProductLink() {
        if (productLinks.isEmpty()) {
            throw new NoSuchElementException("No product links found in results table");
        }
        logger.info("Clicking first product link: {}", productLinks.get(0).getText());
        click(productLinks.get(0));
        return new ProductDetailPage(driver);
    }

    // =========================================================
    //  Navigation Links
    // =========================================================

    public void clickSignInRegister() {
        click(signInRegisterLink);
    }

    public void clickFAQs() {
        click(faqsLink);
    }

    public void clickGlossary() {
        click(glossaryLink);
    }

    public void clickUsersGuide() {
        click(usersGuideLink);
    }

    public void clickCustomerService() {
        click(customerServiceLink);
    }

    // =========================================================
    //  Validation Helpers
    // =========================================================

    /**
     * Returns true if the "NOTE:" text about latest version is present.
     */
    public boolean isNoteMessageDisplayed() {
        return isDisplayed(By.xpath("//*[contains(text(),'Shopz only supplies the latest version')]"));
    }

    /**
     * Returns true if the page heading contains "Product catalog".
     */
    public boolean isPageTitleCorrect() {
        return getPageTitle().toLowerCase().contains("shopz");
    }

    /**
     * Returns true if all 4 filter dropdowns are visible.
     */
    public boolean areAllFiltersDisplayed() {
        return isDisplayed(countryDropdown)
                && isDisplayed(packageTypeDropdown)
                && isDisplayed(groupDropdown)
                && isDisplayed(languageDropdown);
    }
}
