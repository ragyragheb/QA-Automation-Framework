package com.qaautomationframework.qa.stepdefinitions;

import com.qaautomationframework.qa.config.ConfigReader;
import com.qaautomationframework.qa.pages.AmazonCartPage;
import com.qaautomationframework.qa.pages.AmazonCheckoutPage;
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
    private AmazonCheckoutPage checkoutPage;
    private double cartSubtotal;

    @Before("not @api")
    public void setUp() {
        DriverManager.initializeDriver();
        driver = DriverManager.getDriver();
        loginPage = new AmazonLoginPage(driver);
        videoGamesPage = new AmazonVideoGamesPage(driver);
        cartPage = new AmazonCartPage(driver);
        checkoutPage = new AmazonCheckoutPage(driver);
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

    @When("User proceeds to checkout")
    public void userProceedsToCheckout() {
        cartPage.navigateToCart();
        cartSubtotal = cartPage.getItemsTotal();
        System.out.println("Cart Items Subtotal: " + cartSubtotal + " EGP");
        cartPage.proceedToCheckout();
    }

    @When("User adds a new shipping address")
    public void userSelectsAShippingAddress() {
        checkoutPage.dismissPrimeOfferIfPresent();
        checkoutPage.addNewAddress("Ragy Ragheb", "01094778318", "Abdou Pasha", "6",
                "El-Abaseya", "Abbaseyah Square", "Abbaseyah Metro Station");
    }

    @When("User selects Buy Now Pay Later with Valu as payment method")
    public void userSelectsBuyNowPayLaterWithValuAsPaymentMethod() {
        checkoutPage.selectBuyNowPayLaterWithValu();
    }

    @Then("User verifies total amount matches items total plus shipping fees")
    public void userVerifiesTotalAmountMatchesItemsTotalPlusShippingFees() {
        double shippingFee = checkoutPage.getShippingFee();
        double orderTotal = checkoutPage.getOrderTotal();

        System.out.println("Cart Subtotal (from cart page): " + cartSubtotal + " EGP");

        boolean isValid = checkoutPage.verifyTotalAmount(cartSubtotal);
        assert isValid : "Order total (" + orderTotal + " EGP) does not match cart subtotal ("
                + cartSubtotal + " EGP) + shipping fee (" + shippingFee + " EGP) = "
                + (cartSubtotal + shippingFee) + " EGP";
    }

    @Then("User should delete added products from cart")
    public void userShouldDeleteAddedProductsFromCart() {
        videoGamesPage.deleteAddedProductsFromCart();
    }

}
