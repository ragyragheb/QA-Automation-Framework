package com.qaautomationframework.qa.utils;

import com.qaautomationframework.qa.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WebElementUtils {
    private static final Logger logger = LogManager.getLogger(WebElementUtils.class);
    private static final int EXPLICIT_WAIT = ConfigReader.getExplicitWait();

    public static WebElement waitForElementToBeClickable(WebDriver driver, By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT));
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            logger.error("Element not clickable: " + locator.toString());
            throw e;
        }
    }

    public static WebElement waitForElementToBeVisible(WebDriver driver, By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("Element not visible: " + locator.toString());
            throw e;
        }
    }

    public static List<WebElement> waitForElementsToBeVisible(WebDriver driver, By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT));
            return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (TimeoutException e) {
            logger.error("Elements not visible: " + locator.toString());
            throw e;
        }
    }

    public static void clickElement(WebDriver driver, By locator) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                WebElement element = waitForElementToBeClickable(driver, locator);
                // Try regular click first
                try {
                    element.click();
                    logger.info("Clicked element: " + locator.toString());
                    return;
                } catch (ElementClickInterceptedException e) {
                    logger.warn("Element click intercepted, using JavaScript click. Attempt: " + (attempts + 1));
                    // Fall back to JavaScript click
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].click();", element);
                    logger.info("Clicked element using JavaScript: " + locator.toString());
                    return;
                }
            } catch (StaleElementReferenceException e) {
                logger.warn("Stale element, retrying... Attempt: " + (attempts + 1));
                attempts++;
            } catch (Exception e) {
                logger.error("Error clicking element: " + e.getMessage());
                throw e;
            }
        }
        throw new RuntimeException("Failed to click element after 3 attempts: " + locator.toString());
    }

    public static void sendKeys(WebDriver driver, By locator, String text) {
        try {
            WebElement element = waitForElementToBeVisible(driver, locator);
            element.clear();
            element.sendKeys(text);
            logger.info("Entered text '" + text + "' in element: " + locator.toString());
        } catch (Exception e) {
            logger.error("Failed to send keys: " + e.getMessage());
            throw e;
        }
    }

    public static String getText(WebDriver driver, By locator) {
        WebElement element = waitForElementToBeVisible(driver, locator);
        String text = element.getText();
        logger.info("Retrieved text: " + text);
        return text;
    }

    public static void scrollToElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        logger.info("Scrolled to element");
    }

    public static void selectDropdownByVisibleText(WebDriver driver, By locator, String visibleText) {
        WebElement element = waitForElementToBeVisible(driver, locator);
        Select select = new Select(element);
        select.selectByVisibleText(visibleText);
        logger.info("Selected dropdown option: " + visibleText);
    }

    public static boolean isElementDisplayed(WebDriver driver, By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static void waitForElementToDisappear(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        logger.info("Element disappeared: " + locator.toString());
    }

    public static void moveToElement(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
        logger.info("Moved to element");
    }

    public static List<WebElement> getElements(WebDriver driver, By locator) {
        return driver.findElements(locator);
    }

    public static void clickElements(WebDriver driver, By locator) {
        List<WebElement> elements = waitForElementsToBeVisible(driver, locator);
        int totalElements = elements.size();
        int clickedCount = 0;

        while (clickedCount < totalElements) {
            int attempts = 0;
            boolean clicked = false;

            while (attempts < 3 && !clicked) {
                try {
                    List<WebElement> currentElements = driver.findElements(locator);
                    if (currentElements.isEmpty()) {
                        logger.info("No more elements to click");
                        return;
                    }

                    // Always click the first element since previous ones are removed after click
                    WebElement element = currentElements.get(0);
                    scrollToElement(driver, element);
                    element.click();
                    clicked = true;
                    clickedCount++;
                    logger.info("Clicked element " + clickedCount + " of " + totalElements);

                } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                    attempts++;
                    if (attempts >= 3) {
                        logger.error("Failed to click element after 3 attempts: " + e.getMessage());
                        throw new RuntimeException("Failed to click element", e);
                    }
                    logger.warn("Retry attempt " + attempts);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        logger.info("Clicked all " + totalElements + " elements matching: " + locator.toString());
    }

}
