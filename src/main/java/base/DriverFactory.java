package com.ibm.shopz.base;

import com.ibm.shopz.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * DriverFactory - Creates and manages WebDriver instances per-thread (thread-safe).
 * Supports Chrome, Firefox, Edge with optional headless mode.
 * Uses WebDriverManager for automatic driver binary management.
 */
public class DriverFactory {

    private static final Logger logger = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConfigReader config = ConfigReader.getInstance();

    private DriverFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Initializes WebDriver based on browser specified in config.properties.
     *
     * @param browser browser name (chrome / firefox / edge)
     */
    public static void initDriver(String browser) {
        boolean headless = config.isHeadless();
        WebDriver driver;

        switch (browser.toLowerCase().trim()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                logger.info("Firefox WebDriver initialized (headless={})", headless);
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) {
                    edgeOptions.addArguments("--headless");
                }
                driver = new EdgeDriver(edgeOptions);
                logger.info("Edge WebDriver initialized (headless={})", headless);
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                driver = new ChromeDriver(chromeOptions);
                logger.info("Chrome WebDriver initialized (headless={})", headless);
                break;
        }

        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));

        driverThreadLocal.set(driver);
        logger.info("WebDriver stored in ThreadLocal for thread: {}", Thread.currentThread().getName());
    }

    /**
     * Returns the WebDriver instance for the current thread.
     */
    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            throw new IllegalStateException("WebDriver has not been initialized for thread: "
                    + Thread.currentThread().getName());
        }
        return driverThreadLocal.get();
    }

    /**
     * Quits the WebDriver and removes it from ThreadLocal.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            logger.info("WebDriver quit and removed from ThreadLocal for thread: {}",
                    Thread.currentThread().getName());
        }
    }
}
