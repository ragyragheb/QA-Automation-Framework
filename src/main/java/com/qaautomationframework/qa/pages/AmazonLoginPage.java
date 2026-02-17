package com.qaautomationframework.qa.pages;

import com.qaautomationframework.qa.utils.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AmazonLoginPage extends BasePage {

    // Locators
    private final By accountListsLink = By.id("nav-link-accountList");
    private final By emailInput = By.id("ap_email_login");
    private final By continueButton = By.id("continue");
    private final By passwordInput = By.id("ap_password");
    private final By signInButton = By.id("signInSubmit");
    private final By accountName = By.id("nav-link-accountList-nav-line-1");

    public AmazonLoginPage() {
        super();
    }

    public AmazonLoginPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToLogin(String url) {
        navigateToUrl(url);
        logger.info("Navigated to Amazon homepage");
        WebElementUtils.clickElement(driver, accountListsLink);
        logger.info("Clicked on Account & Lists");
    }

    public void enterEmail(String email) {
        WebElementUtils.sendKeys(driver, emailInput, email);
        logger.info("Entered email: " + email);
    }

    public void clickContinue() {
        WebElementUtils.clickElement(driver, continueButton);
        logger.info("Clicked Continue button");
    }

    public void enterPassword(String password) {
        WebElementUtils.sendKeys(driver, passwordInput, password);
        logger.info("Entered password");
    }

    public void clickSignIn() {
        WebElementUtils.clickElement(driver, signInButton);
        logger.info("Clicked Sign In button");
    }

    public void login(String url, String email, String password) {
        navigateToLogin(url);
        enterEmail(email);
        clickContinue();
        waitForPageLoad();
        enterPassword(password);
        clickSignIn();
        waitForPageLoad();
        logger.info("Login completed successfully");
    }

    public boolean isUserLoggedIn() {
        return WebElementUtils.isElementDisplayed(driver, accountName);
    }
}
