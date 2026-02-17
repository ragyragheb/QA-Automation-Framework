package com.qaautomationframework.qa.pages;

import com.qaautomationframework.qa.utils.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class AmazonCartPage extends BasePage {

    private final By cartIcon = By.id("nav-cart");
    private final By cartItems = By.cssSelector("div.sc-list-item");
    private final By cartItemName = By.cssSelector("span.a-truncate-full.a-offscreen");
    private final By cartItemPrice = By.cssSelector("span.apex-price-to-pay-value");
    private final By cartTotal = By.cssSelector("span.sc-price");
    private final By proceedToCheckoutButton = By.cssSelector("input[name='proceedToRetailCheckout']");

    public AmazonCartPage() {
        super();
    }

    public AmazonCartPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToCart() {
        WebElementUtils.clickElement(driver, cartIcon);
        waitForPageLoad();
        logger.info("Navigated to cart");
    }

    public List<String> getCartItems() {
        List<String> itemNames = new ArrayList<>();
        List<WebElement> items = WebElementUtils.getElements(driver, cartItems);
        
        for (WebElement item : items) {
            try {
                WebElement nameElement = item.findElement(cartItemName);
                itemNames.add(nameElement.getText());
            } catch (Exception e) {
                logger.warn("Error getting item name: " + e.getMessage());
            }
        }
        
        logger.info("Found " + itemNames.size() + " items in cart");
        return itemNames;
    }

    public boolean validateCartTotal() {
        double itemsTotal = getItemsTotal();
        double cartTotalValue = getCartTotal();

        logger.info("Validating cart total. Items Total: " + itemsTotal + " EGP, Cart Total: " + cartTotalValue + " EGP");
 
        return Math.abs(itemsTotal - cartTotalValue) < 0.01;
    }

    public void proceedToCheckout() {
        WebElementUtils.clickElement(driver, proceedToCheckoutButton);
        waitForPageLoad();
        logger.info("Clicked Proceed to Checkout");
    }

    public int getCartItemCount() {
        List<WebElement> items = WebElementUtils.getElements(driver, cartItems);
        int count = items.size();
        logger.info("Cart has " + count + " items");
        return count;
    }

    public double getItemsTotal() {
        try {
            List<WebElement> items = WebElementUtils.getElements(driver, cartItems);
            double itemsTotal = 0.0;

            for (WebElement item : items) {
                try {
                    List<WebElement> priceElements = item.findElements(cartItemPrice);
                    if (!priceElements.isEmpty()) {
                        String priceText = priceElements.get(0).getText().trim();
                        logger.info("Raw item price text: '" + priceText + "'");

                        String cleanText = priceText
                                .replaceAll("EGP", "")
                                .replaceAll("\\s+", "")
                                .replaceAll(",", "")
                                .trim();

                        if (!cleanText.contains(".") && cleanText.length() > 2) {
                            cleanText = cleanText.substring(0, cleanText.length() - 2) + "." + cleanText.substring(cleanText.length() - 2);
                        }

                        logger.info("Cleaned item price text: '" + cleanText + "'");
                        double price = Double.parseDouble(cleanText);
                        itemsTotal += price;
                    }
                } catch (Exception e) {
                    logger.warn("Could not parse item price: " + e.getMessage());
                }
            }

            return itemsTotal;
        } catch (Exception e) {
            logger.error("Error calculating items total: " + e.getMessage());
            return 0.0;
        }
    }

    public double getCartTotal() {
        try {
            WebElement cartTotalElement = WebElementUtils.waitForElementToBeVisible(driver, cartTotal);
            String cartTotalText = cartTotalElement.getText().trim();
            logger.info("Raw cart total text: '" + cartTotalText + "'");

            // Remove currency symbols and thousands separator (comma), keep decimal point
            String cleanText = cartTotalText.replaceAll("[^0-9,.]", "").replaceAll(",", "").trim();
            logger.info("Cleaned cart total text: '" + cleanText + "'");

            if (cleanText.isEmpty()) {
                logger.warn("Cart total text is empty after cleaning");
                return 0.0;
            }

            return Double.parseDouble(cleanText);
        } catch (Exception e) {
            logger.error("Error getting cart total: " + e.getMessage());
            return 0.0;
        }
    }
}
