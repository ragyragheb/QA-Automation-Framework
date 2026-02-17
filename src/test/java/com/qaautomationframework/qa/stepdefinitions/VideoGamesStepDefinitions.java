package com.qaautomationframework.qa.stepdefinitions;

import com.qaautomationframework.qa.config.ConfigReader;
import com.qaautomationframework.qa.pages.AmazonCartPage;
import com.qaautomationframework.qa.pages.AmazonLoginPage;
import com.qaautomationframework.qa.pages.AmazonVideoGamesPage;
import com.qaautomationframework.qa.utils.DriverManager;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;

public class VideoGamesStepDefinitions {

    private WebDriver driver;
    private AmazonLoginPage loginPage;
    private AmazonVideoGamesPage videoGamesPage;
    private AmazonCartPage cartPage;

    @Before("not @api")
    public void setUp() {
        DriverManager.initializeDriver();
        driver = DriverManager.getDriver();
        loginPage = new AmazonLoginPage(driver);
        videoGamesPage = new AmazonVideoGamesPage(driver);
        cartPage = new AmazonCartPage(driver);
    }

    @After("not @api")
    public void tearDown() {
        DriverManager.quitDriver();
    }

    @Given("User opens Amazon website")
    public void userOpensAmazonWebsite() {
        driver.get("https://www.amazon.eg/");
        // Optionally wait for page to load
    }

    @Given("User logs in to Amazon")
    public void userLogsInToAmazon() {
        String url = ConfigReader.getAmazonUrl();
        String email = ConfigReader.getAmazonEmail();
        String password = ConfigReader.getAmazonPassword();
        
        loginPage.login(url, email, password);
    }

    @When("User navigates to Video Games category")
    public void userNavigatesToVideoGamesCategory() {
        videoGamesPage.openAllMenu();
        videoGamesPage.navigateToVideoGames();
        videoGamesPage.clickAllVideoGames();
    }

    @When("User applies Free Shipping filter")
    public void userAppliesFreeShippingFilter() {
        videoGamesPage.applyFreeShippingFilter();
    }

    @When("User applies New Condition filter")
    public void userAppliessNewConditionFilter() {
        videoGamesPage.applyNewConditionFilter();
    }

    @When("User sorts by Price High to Low")
    public void userSortsByPriceHighToLow() {
        videoGamesPage.sortByPriceHighToLow();
    }

    @When("User adds products below 15K to cart")
    public void userAddsProductsBelowToCart() {
        videoGamesPage.addProductsBelow15K(5); // Remove the limit (5) if you want to add all products below 15K
    }

    @Then("User should have added products to cart")
    public void userShouldHaveAddedProductsToCart() {
        int productCount = videoGamesPage.getAddedProductNames().size();
        assert productCount > 0 : "No products were added to cart";
    }

    @Then("User validates items total against cart total")
    public void userValidatesItemsTotalAgainstCartTotal() {
        cartPage.navigateToCart();

        double itemsTotal = cartPage.getItemsTotal();
        double cartTotal = cartPage.getCartTotal();

        System.out.println("Items Total: " + itemsTotal + " EGP");
        System.out.println("Cart Total: " + cartTotal + " EGP");

        boolean isValid = cartPage.validateCartTotal();
        assert isValid : "Cart total does not match items total";
    }

    @Then("User should delete added products from cart")
    public void userShouldDeleteAddedProductsFromCart() {
        videoGamesPage.deleteAddedProductsFromCart();
    }

}
