package com.ibm.shopz.pages;

import com.ibm.shopz.base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

/**
 * FmidDetailPage - Page Object for the FMID detail page.
 *
 * Loaded when user clicks a FMID link from the Product Catalog results table.
 * From the doc screenshots, this page shows:
 *  - FMID name/number
 *  - Associated product name and number (e.g. 5655-GOZ)
 *  - Package details, release info
 *  - Product link that navigates to ProductDetailPage
 */
public class FmidDetailPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(FmidDetailPage.class);

    // =========================================================
    //  Page Elements
    // =========================================================

    /** Page heading / FMID identifier */
    @FindBy(css = "h1, h2, .fmid-title, #content h1")
    private WebElement pageHeading;

    /** FMID number displayed on page */
    @FindBy(xpath = "//*[contains(@class,'fmid') or contains(@id,'fmid')]")
    private WebElement fmidNumber;

    /** Product number link (e.g. 5655-GOZ) */
    @FindBy(xpath = "//a[contains(@href,'prodnum') or contains(@href,'prodlink')]")
    private List<WebElement> productLinks;

    /** All links on this page */
    @FindBy(tagName = "a")
    private List<WebElement> allLinks;

    /** Table rows with package/FMID info */
    @FindBy(xpath = "//table//tr")
    private List<WebElement> tableRows;

    /** Back / Breadcrumb link to product catalog */
    @FindBy(linkText = "Product catalog")
    private WebElement backToCatalogLink;

    /** Release / version info */
    @FindBy(xpath = "//*[contains(@class,'release') or contains(text(),'Release')]")
    private WebElement releaseInfo;

    // =========================================================
    //  Constructor
    // =========================================================

    public FmidDetailPage(WebDriver driver) {
        super(driver);
        waitForPageLoad();
        logger.info("FmidDetailPage initialized. Current URL: {}", driver.getCurrentUrl());
    }

    // =========================================================
    //  Accessors
    // =========================================================

    /**
     * Get the text of the page heading.
     */
    public String getPageHeadingText() {
        try {
            return getText(pageHeading);
        } catch (Exception e) {
            logger.warn("Could not read FMID page heading: {}", e.getMessage());
            return driver.getTitle();
        }
    }

    /**
     * Check if the page has loaded correctly (has some content).
     */
    public boolean isPageLoaded() {
        return isPageTitleCorrect() || !tableRows.isEmpty();
    }

    /**
     * Returns true if the page title/heading is non-empty.
     */
    public boolean isPageTitleCorrect() {
        return !getPageTitle().isEmpty();
    }

    /**
     * Get all product links on this page.
     */
    public List<WebElement> getProductLinks() {
        return productLinks;
    }

    /**
     * Get text of all product links.
     */
    public List<String> getProductLinkTexts() {
        List<String> texts = new ArrayList<>();
        productLinks.forEach(link -> texts.add(link.getText().trim()));
        return texts;
    }

    /**
     * Get all table row data as text.
     */
    public List<String> getAllTableRowTexts() {
        List<String> rows = new ArrayList<>();
        tableRows.forEach(row -> {
            String text = row.getText().trim();
            if (!text.isEmpty()) rows.add(text);
        });
        return rows;
    }

    /**
     * Get the current page URL.
     */
    public String getPageUrl() {
        return getCurrentUrl();
    }

    /**
     * Click a product link by product number (e.g. "5655-GOZ").
     *
     * @param productNumber the product number text
     * @return ProductDetailPage
     */
    public ProductDetailPage clickProductLink(String productNumber) {
        WebElement link = productLinks.stream()
                .filter(l -> l.getText().trim().equalsIgnoreCase(productNumber))
                .findFirst()
                .orElseThrow(() -> new org.openqa.selenium.NoSuchElementException(
                        "Product link not found: " + productNumber));
        logger.info("Clicking product link: {} from FMID detail page", productNumber);
        click(link);
        return new ProductDetailPage(driver);
    }

    /**
     * Click the first product link found on the page.
     */
    public ProductDetailPage clickFirstProductLink() {
        if (productLinks.isEmpty()) {
            throw new org.openqa.selenium.NoSuchElementException(
                    "No product links found on FMID detail page");
        }
        logger.info("Clicking first product link: {}", productLinks.get(0).getText());
        click(productLinks.get(0));
        return new ProductDetailPage(driver);
    }

    /**
     * Navigate back to Product Catalog page.
     */
    public ProductCatalogPage backToProductCatalog() {
        try {
            click(backToCatalogLink);
        } catch (Exception e) {
            logger.warn("Could not click back link, using browser back: {}", e.getMessage());
            driver.navigate().back();
        }
        return new ProductCatalogPage(driver);
    }

    /**
     * Check if a specific product number is listed on this page.
     */
    public boolean isProductNumberPresent(String productNumber) {
        return isDisplayed(By.xpath(
                "//a[contains(text(),'" + productNumber + "')] | " +
                "//*[contains(text(),'" + productNumber + "')]"));
    }
}
