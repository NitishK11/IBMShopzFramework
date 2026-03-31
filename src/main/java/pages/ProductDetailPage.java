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
 * ProductDetailPage - Page Object for the IBM ShopZ Product Detail page.
 *
 * Reached after clicking a product link (e.g. 5655-GOZ) from:
 *   - ProductCatalogPage results table
 *   - FmidDetailPage
 *
 * From the doc screenshots (Screen after click on Prod Link 5655-GOZ),
 * this page shows:
 *  - Product name and number
 *  - Product description / overview
 *  - Available FMIDs for this product
 *  - Package type, release, language info
 *  - Order / download links
 */
public class ProductDetailPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(ProductDetailPage.class);

    // =========================================================
    //  Page Elements
    // =========================================================

    /** Product name / page heading */
    @FindBy(css = "h1, h2, .product-title, #content h1, #content h2")
    private WebElement productNameHeading;

    /** Product number (e.g. 5655-GOZ) */
    @FindBy(xpath = "//*[contains(@class,'prodnum') or contains(@id,'prodnum')]")
    private WebElement productNumber;

    /** Product description section */
    @FindBy(xpath = "//*[contains(@class,'description') or contains(@id,'description')]")
    private WebElement productDescription;

    /** FMID list items or table */
    @FindBy(xpath = "//table//tr[contains(.,'FMID')]//td | //li[contains(.,'FMID')]")
    private List<WebElement> fmidItems;

    /** All FMID links on this page */
    @FindBy(xpath = "//a[contains(@href,'fmid') or contains(@href,'FMID')]")
    private List<WebElement> fmidLinks;

    /** Order / Add to cart button */
    @FindBy(xpath = "//input[@type='submit'][contains(@value,'Order') or contains(@value,'Add')] "
            + "| //button[contains(text(),'Order') or contains(text(),'Add to cart')]")
    private WebElement orderButton;

    /** Package type info */
    @FindBy(xpath = "//*[contains(text(),'Package') or contains(text(),'package')]")
    private WebElement packageTypeInfo;

    /** Release info */
    @FindBy(xpath = "//*[contains(text(),'Release') or contains(text(),'Version')]")
    private WebElement releaseInfo;

    /** All table rows with product details */
    @FindBy(xpath = "//table//tr")
    private List<WebElement> detailRows;

    /** Back to catalog navigation */
    @FindBy(linkText = "Product catalog")
    private WebElement backToCatalogLink;

    /** All links on page */
    @FindBy(tagName = "a")
    private List<WebElement> allLinks;

    // =========================================================
    //  Constructor
    // =========================================================

    public ProductDetailPage(WebDriver driver) {
        super(driver);
        waitForPageLoad();
        logger.info("ProductDetailPage initialized. URL: {}", driver.getCurrentUrl());
    }

    // =========================================================
    //  Accessors
    // =========================================================

    /**
     * Get the product name / page heading text.
     */
    public String getProductNameHeading() {
        try {
            return getText(productNameHeading);
        } catch (Exception e) {
            logger.warn("Could not get product name heading: {}", e.getMessage());
            return getPageTitle();
        }
    }

    /**
     * Get the product number displayed on the page.
     */
    public String getProductNumberText() {
        try {
            return getText(productNumber);
        } catch (Exception e) {
            logger.warn("Could not get product number: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Returns true if the page loaded (non-empty title or rows).
     */
    public boolean isPageLoaded() {
        return !getPageTitle().isEmpty() || !detailRows.isEmpty();
    }

    /**
     * Get all FMID links on the product detail page.
     */
    public List<WebElement> getFmidLinks() {
        return fmidLinks;
    }

    /**
     * Get all FMID link text values.
     */
    public List<String> getFmidLinkTexts() {
        List<String> texts = new ArrayList<>();
        fmidLinks.forEach(link -> texts.add(link.getText().trim()));
        return texts;
    }

    /**
     * Get all detail table rows as text.
     */
    public List<String> getAllDetailRowTexts() {
        List<String> rows = new ArrayList<>();
        detailRows.forEach(row -> {
            String text = row.getText().trim();
            if (!text.isEmpty()) rows.add(text);
        });
        return rows;
    }

    /**
     * Get current page URL.
     */
    public String getPageUrl() {
        return getCurrentUrl();
    }

    /**
     * Check if Order button is present.
     */
    public boolean isOrderButtonPresent() {
        return isDisplayed(orderButton);
    }

    /**
     * Check if the product detail page contains a specific text.
     */
    public boolean isTextPresent(String text) {
        return isDisplayed(By.xpath("//*[contains(text(),'" + text + "')]"));
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
     * Click an FMID link by its text on this product detail page.
     */
    public FmidDetailPage clickFmidLink(String fmidText) {
        WebElement link = fmidLinks.stream()
                .filter(l -> l.getText().trim().equalsIgnoreCase(fmidText))
                .findFirst()
                .orElseThrow(() -> new org.openqa.selenium.NoSuchElementException(
                        "FMID link not found: " + fmidText));
        click(link);
        return new FmidDetailPage(driver);
    }
}
