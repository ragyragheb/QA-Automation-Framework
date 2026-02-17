package com.qaautomationframework.qa.pages;

import com.qaautomationframework.qa.utils.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BasePage {
    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;

    public BasePage() {
        this.driver = DriverManager.getDriver();
    }

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    public String getPageTitle() {
        String title = driver.getTitle();
        logger.info("Current page title: " + title);
        return title;
    }

    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.info("Current URL: " + url);
        return url;
    }

    public void navigateToUrl(String url) {
        driver.get(url);
        logger.info("Navigated to: " + url);
    }

    public void waitForPageLoad() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(webDriver -> {
                JavascriptExecutor js = (JavascriptExecutor) webDriver;
                return js.executeScript("return document.readyState").equals("complete");
            });
            logger.info("Page loaded completely");
        } catch (Exception e) {
            logger.warn("Page load wait timed out or failed: " + e.getMessage());
        }
    }
}
