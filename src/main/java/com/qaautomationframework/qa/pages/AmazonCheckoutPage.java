package com.qaautomationframework.qa.pages;

import com.qaautomationframework.qa.utils.WebElementUtils;
import org.openqa.selenium.By;

public class AmazonCheckoutPage extends BasePage {

    private final By addressForm = By.id("address-ui-widgets-form-submit-button");
    private final By useThisAddressButton = By.cssSelector("input[data-testid='Address_selectShipToThisAddress']");
    
    private final By cashOnDeliveryOption = By.xpath("//input[@value='instrumentId=Cash']");
    private final By useThisPaymentButton = By.cssSelector("input[name='ppw-widgetEvent:SetPaymentPlanSelectContinueEvent']");
    
    private final By orderTotal = By.cssSelector("span.grand-total-price");
    private final By shippingFee = By.xpath("//span[contains(text(),'Shipping')]/following-sibling::span");
    private final By itemsSubtotal = By.cssSelector("span.a-color-price.value");

    public void selectAddress() {
        try {
            if (WebElementUtils.isElementDisplayed(driver, useThisAddressButton)) {
                WebElementUtils.clickElement(driver, useThisAddressButton);
                logger.info("Selected existing address");
            } else if (WebElementUtils.isElementDisplayed(driver, addressForm)) {
                WebElementUtils.clickElement(driver, addressForm);
                logger.info("Submitted address form");
            }
            waitForPageLoad();
        } catch (Exception e) {
            logger.warn("Address selection issue: " + e.getMessage());
        }
    }

    public void selectCashOnDelivery() {
        try {
            WebElementUtils.clickElement(driver, cashOnDeliveryOption);
            logger.info("Selected Cash on Delivery");
            waitForPageLoad();
            
            if (WebElementUtils.isElementDisplayed(driver, useThisPaymentButton)) {
                WebElementUtils.clickElement(driver, useThisPaymentButton);
                logger.info("Confirmed payment method");
            }
            waitForPageLoad();
        } catch (Exception e) {
            logger.warn("Payment selection issue: " + e.getMessage());
        }
    }

    public double getOrderTotal() {
        try {
            String totalText = WebElementUtils.getText(driver, orderTotal)
                .replace("EGP", "")
                .replace(",", "")
                .trim();
            double total = Double.parseDouble(totalText);
            logger.info("Order total: " + total + " EGP");
            return total;
        } catch (Exception e) {
            logger.error("Failed to get order total: " + e.getMessage());
            return 0.0;
        }
    }

    public double getShippingFee() {
        try {
            if (WebElementUtils.isElementDisplayed(driver, shippingFee)) {
                String feeText = WebElementUtils.getText(driver, shippingFee)
                    .replace("EGP", "")
                    .replace(",", "")
                    .trim();
                double fee = Double.parseDouble(feeText);
                logger.info("Shipping fee: " + fee + " EGP");
                return fee;
            } else {
                logger.info("No shipping fee (Free shipping)");
                return 0.0;
            }
        } catch (Exception e) {
            logger.warn("Failed to get shipping fee, assuming free shipping");
            return 0.0;
        }
    }

    public double getItemsSubtotal() {
        try {
            String subtotalText = WebElementUtils.getText(driver, itemsSubtotal)
                .replace("EGP", "")
                .replace(",", "")
                .trim();
            double subtotal = Double.parseDouble(subtotalText);
            logger.info("Items subtotal: " + subtotal + " EGP");
            return subtotal;
        } catch (Exception e) {
            logger.error("Failed to get items subtotal: " + e.getMessage());
            return 0.0;
        }
    }

    public boolean verifyTotalAmount(double cartSubtotal) {
        double shippingFee = getShippingFee();
        double expectedTotal = cartSubtotal + shippingFee;
        double actualTotal = getOrderTotal();
        
        boolean isCorrect = Math.abs(expectedTotal - actualTotal) < 0.01;
        
        if (isCorrect) {
            logger.info("Total amount verified: " + actualTotal + " EGP (Subtotal: " + 
                       cartSubtotal + " + Shipping: " + shippingFee + ")");
        } else {
            logger.error("Total amount mismatch! Expected: " + expectedTotal + 
                        " EGP, Actual: " + actualTotal + " EGP");
        }
        
        return isCorrect;
    }
}
