package com.qaautomationframework.qa.pages;

import com.qaautomationframework.qa.utils.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AmazonCheckoutPage extends BasePage {

    public AmazonCheckoutPage() {
        super();
    }

    public AmazonCheckoutPage(WebDriver driver) {
        super(driver);
    }

    private final By primeOfferNoThanks = By.cssSelector("a.a-button-text");

    private final By changeAddressButton = By.xpath("//a[contains(@data-csa-c-slot-id,'checkout-change-shipaddressselect')]");
    private final By addNewAddressButton = By.id("add-new-address-desktop-sasp-tango-link");

    private final By fullNameField = By.id("address-ui-widgets-enterAddressFullName");
    private final By phoneField = By.id("address-ui-widgets-enterAddressPhoneNumber");
    private final By streetField = By.id("address-ui-widgets-enterAddressLine1");
    private final By buildingField = By.id("address-ui-widgets-enter-building-name-or-number");
    private final By cityDropdown = By.xpath("//input[contains(@id,'address-ui-widgets-enterAddressCity')]");
    private final By districtDropdown = By.id("address-ui-widgets-enterAddressDistrictOrCounty");
    private final By autoCompleteSuggestion = By.id("address-ui-widgets-autoCompleteResult-0");
    private final By nearestLandmarkField = By.id("address-ui-widgets-landmark");
    private final By continueButton = By.xpath("//input[contains(@data-testid,'continue-button')]");

    private final By buyNowPayLaterWithValu = By.xpath("//input[contains(@value,'Loan')]");
    private final By useThisPaymentButton = By.cssSelector("input[name='ppw-widgetEvent:SetPaymentPlanSelectContinueEvent']");
    
    private final By orderTotal = By.xpath("(//div/div[(contains(@class,'order-summary-line-definition'))])[4]");
    private final By shippingFee = By.xpath("//input[contains(@value,'SHIPPING_TAX_INCLUSIVE')]/following-sibling::span");
    private final By itemsSubtotal = By.xpath("//input[contains(@value,'ITEMS_TAX_INCLUSIVE')]/following-sibling::span");
    private final By freeShippingLabel = By.xpath("//div[contains(text(),'Free Delivery')]");

    public void dismissPrimeOfferIfPresent() {
        if (WebElementUtils.isElementDisplayed(driver, primeOfferNoThanks)) {
            WebElementUtils.clickElement(driver, primeOfferNoThanks);
            logger.info("Dismissed Amazon Prime offer");
            waitForPageLoad();
        }
    }

    public void addNewAddress(String fullName, String phone, String street, String building,
                              String city, String district, String landmark) {
        try {
            WebElementUtils.clickElement(driver, changeAddressButton);
            WebElementUtils.clickElement(driver, addNewAddressButton);
            logger.info("Clicked Add New Address button");
            waitForPageLoad();

            WebElementUtils.sendKeys(driver, fullNameField, fullName);
            WebElementUtils.sendKeys(driver, phoneField, phone);
            WebElementUtils.sendKeys(driver, streetField, street);
            WebElementUtils.sendKeys(driver, buildingField, building);
            WebElementUtils.selectAutocompleteSuggestion(driver, cityDropdown, autoCompleteSuggestion, city);
            WebElementUtils.selectAutocompleteSuggestion(driver, districtDropdown, autoCompleteSuggestion, district);
            WebElementUtils.sendKeys(driver, nearestLandmarkField, landmark);

            WebElementUtils.clickElement(driver, continueButton);
            logger.info("Submitted new address for: " + fullName);
            waitForPageLoad();
        } catch (Exception e) {
            logger.error("Failed to add new address: " + e.getMessage());
        }
    }

    public void selectBuyNowPayLaterWithValu() {
        try {
            WebElementUtils.waitForElementToBeVisible(driver, buyNowPayLaterWithValu);
            WebElementUtils.clickElement(driver, buyNowPayLaterWithValu);
            logger.info("Selected Buy Now Pay Later with Valu");
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
        double itemsSubtotal = getItemsSubtotal();

        if (WebElementUtils.isElementDisplayed(driver, freeShippingLabel)) {
            logger.info("Free shipping applied, setting shipping fee to 0");
            shippingFee = 0;
        }

        double expectedTotal = itemsSubtotal + shippingFee;
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
