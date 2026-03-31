package com.ibm.shopz.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.ibm.shopz.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ExtentReportManager - Manages ExtentReports 5 lifecycle.
 *
 * Responsibilities:
 *  - Initialize ExtentReports with SparkReporter (HTML)
 *  - Create test nodes
 *  - Flush report at suite end
 *  - Thread-safe via ThreadLocal<ExtentTest>
 */
public class ExtentReportManager {

    private static final Logger logger = LogManager.getLogger(ExtentReportManager.class);

    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
    private static final ConfigReader config = ConfigReader.getInstance();

    private ExtentReportManager() {
        // Utility class
    }

    /**
     * Initialize the ExtentReports instance with SparkReporter.
     * Should be called once in @BeforeSuite.
     */
    public static synchronized void initReport() {
        if (extentReports == null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String reportPath = config.getReportPath()
                    + config.getReportName() + "_" + timestamp + ".html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setDocumentTitle(config.getReportTitle());
            sparkReporter.config().setReportName(config.getReportName());
            sparkReporter.config().setEncoding("UTF-8");
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);

            // System info that appears in the report overview
            extentReports.setSystemInfo("Application", "IBM ShopZ Product Catalog");
            extentReports.setSystemInfo("Environment", "QA");
            extentReports.setSystemInfo("Browser", config.getBrowser());
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("Framework", "Selenium 4 + TestNG + ExtentReports 5");
            extentReports.setSystemInfo("Base URL", config.getBaseUrl());
            extentReports.setSystemInfo("Tester", System.getProperty("user.name"));

            logger.info("ExtentReports initialized. Report path: {}", reportPath);
        }
    }

    /**
     * Create a new test node in the report.
     *
     * @param testName        the test method name
     * @param testDescription description of the test
     * @return ExtentTest instance stored in ThreadLocal
     */
    public static ExtentTest createTest(String testName, String testDescription) {
        ExtentTest test = extentReports.createTest(testName, testDescription);
        extentTestThreadLocal.set(test);
        logger.debug("ExtentTest created for: {}", testName);
        return test;
    }

    /**
     * Create a child node under an existing test (for grouping steps).
     *
     * @param parent parent ExtentTest
     * @param name   child node name
     * @return child ExtentTest
     */
    public static ExtentTest createChildNode(ExtentTest parent, String name) {
        return parent.createNode(name);
    }

    /**
     * Get the ExtentTest for the current thread.
     */
    public static ExtentTest getExtentTest() {
        return extentTestThreadLocal.get();
    }

    /**
     * Remove the ExtentTest from ThreadLocal (cleanup).
     */
    public static void removeExtentTest() {
        extentTestThreadLocal.remove();
    }

    /**
     * Flush the ExtentReports (writes HTML file to disk).
     * Should be called once in @AfterSuite.
     */
    public static synchronized void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReports flushed successfully.");
        }
    }

    /**
     * Get the ExtentReports instance (for advanced usage).
     */
    public static ExtentReports getExtentReports() {
        return extentReports;
    }
}
