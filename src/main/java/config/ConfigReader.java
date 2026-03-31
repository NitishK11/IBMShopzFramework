package com.ibm.shopz.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader - Singleton class to read configuration properties.
 * Loads config.properties from src/test/resources.
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static ConfigReader instance;
    private final Properties properties;

    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";

    private ConfigReader() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
            logger.info("Configuration properties loaded successfully from: {}", CONFIG_FILE_PATH);
        } catch (IOException e) {
            logger.error("Failed to load configuration properties: {}", e.getMessage());
            throw new RuntimeException("Cannot load configuration file: " + CONFIG_FILE_PATH, e);
        }
    }

    /**
     * Returns singleton instance of ConfigReader.
     */
    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    public String getBaseUrl() {
        return properties.getProperty("base.url");
    }

    public String getBrowser() {
        return properties.getProperty("browser", "chrome");
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless", "false"));
    }

    public int getImplicitWait() {
        return Integer.parseInt(properties.getProperty("implicit.wait", "10"));
    }

    public int getExplicitWait() {
        return Integer.parseInt(properties.getProperty("explicit.wait", "20"));
    }

    public int getPageLoadTimeout() {
        return Integer.parseInt(properties.getProperty("page.load.timeout", "30"));
    }

    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(properties.getProperty("screenshot.on.failure", "true"));
    }

    public String getScreenshotPath() {
        return properties.getProperty("screenshot.path", "screenshots/");
    }

    public String getReportPath() {
        return properties.getProperty("report.path", "reports/");
    }

    public String getReportName() {
        return properties.getProperty("report.name", "IBMShopzTestReport");
    }

    public String getReportTitle() {
        return properties.getProperty("report.title", "IBM ShopZ Automation Report");
    }

    public String getDefaultCountry() {
        return properties.getProperty("country.default", "United States");
    }

    public String getCatalogPageUrl() {
        return properties.getProperty("catalog.page.url");
    }

    public String getLoginPageUrl() {
        return properties.getProperty("login.page.url");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
