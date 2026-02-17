package com.qaautomationframework.qa.pages;

import com.qaautomationframework.qa.utils.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class AmazonVideoGamesPage extends BasePage {

    private final By allMenuButton = By.id("nav-hamburger-menu");
    private final By seeAllLink = By.xpath("//div[contains(text(),'See all')]");
    private final By videoGamesCategory = By.xpath("//a[@data-menu-id='16']");
    private final By allVideoGamesLink = By.xpath("(//a[contains(text(),'All Video Games')])[1]");
    private final By freeShippingFilter = By.xpath("//a[contains(@href,'free_shipping')]");
    private final By newConditionFilter = By.xpath("//a[contains(@href,'condition-type_1')]");
    private final By sortDropdown = By.cssSelector(".a-button-text.a-declarative");
    private final By highToLowOption = By.id("s-result-sort-select_2");
    private final By productCards = By.cssSelector("div[data-component-type='s-search-result']");
    private final By productName = By.cssSelector("h2.a-size-base-plus");
    private final By productPrice = By.cssSelector("span.a-price-whole");
    private final By addToCartButton = By.cssSelector("button[name='submit.addToCart']");
    private final By nextPageButton = By.cssSelector("a.s-pagination-next");
    private final By cartIcon = By.cssSelector("a#nav-cart");
    private final By deleteButton = By.xpath("//input[contains(@value,'Delete')]");

    private List<String> addedProductNames = new ArrayList<>();

    public AmazonVideoGamesPage() {
        super();
    }

    public AmazonVideoGamesPage(WebDriver driver) {
        super(driver);
    }

    public void openAllMenu() {
        WebElementUtils.clickElement(driver, allMenuButton);
        waitForPageLoad();
    }

    public void navigateToVideoGames() {
        WebElementUtils.clickElement(driver, seeAllLink);
        waitForPageLoad();
        WebElementUtils.clickElement(driver, videoGamesCategory);
        waitForPageLoad();
    }

    public void clickAllVideoGames() {
        WebElementUtils.waitForElementToBeVisible(driver, allVideoGamesLink);
        WebElementUtils.clickElement(driver, allVideoGamesLink);
        waitForPageLoad();
    }

    public void applyFreeShippingFilter() {
        WebElementUtils.clickElement(driver, freeShippingFilter);
        waitForPageLoad();
    }

    public void applyNewConditionFilter() {
        WebElementUtils.waitForElementToBeVisible(driver, newConditionFilter);
        WebElement element = driver.findElement(newConditionFilter);
        WebElementUtils.scrollToElement(driver, element);
        WebElementUtils.clickElement(driver, newConditionFilter);
        waitForPageLoad();
    }

    public void sortByPriceHighToLow() {
        WebElementUtils.clickElement(driver, sortDropdown);
        waitForPageLoad();
        WebElementUtils.clickElement(driver, highToLowOption);
        waitForPageLoad();
    }

    public void addProductsBelow15K() {
        addProductsBelow15K(Integer.MAX_VALUE);
    }

    public void addProductsBelow15K(int maxItems) {
        boolean continueSearching = true;
        int pageNumber = 1;

        while (continueSearching && addedProductNames.size() < maxItems) {
            List<WebElement> products = WebElementUtils.getElements(driver, productCards);

            boolean foundAnyBelow15k = false;

            for (WebElement product : products) {
                try {
                    if (addedProductNames.size() >= maxItems) {
                        continueSearching = false;
                        break;
                    }

                    List<WebElement> priceElements = product.findElements(productPrice);
                    if (priceElements.isEmpty()) {
                        continue;
                    }

                    String priceText = priceElements.get(0).getText().replaceAll("[,.]", "").trim();
                    if (priceText.isEmpty()) {
                        continue;
                    }

                    double price = Double.parseDouble(priceText);

                    if (price < 15000) {
                        foundAnyBelow15k = true;

                        String productNameText = "Unknown Product";
                        try {
                            List<WebElement> nameElements = product.findElements(productName);
                            if (!nameElements.isEmpty()) {
                                productNameText = nameElements.get(0).getText();
                            }
                        } catch (Exception e) {
                        }

                        try {
                            WebElement addButton = product.findElement(addToCartButton);
                            WebElementUtils.scrollToElement(driver, product);
                            addButton.click();
                            addedProductNames.add(productNameText);
                            logger.info("Added product: " + productNameText + " (Price: " + price + " EGP)");
                        } catch (Exception e) {
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            if (isNextPageAvailable()) {
                goToNextPage();
                pageNumber++;
            } else {
                continueSearching = false;
            }
        }

        logger.info("Total products added to cart: " + addedProductNames.size());
    }

    private boolean isNextPageAvailable() {
        try {
            return WebElementUtils.isElementDisplayed(driver, nextPageButton);
        } catch (Exception e) {
            return false;
        }
    }

    private void goToNextPage() {
        WebElementUtils.clickElement(driver, nextPageButton);
        waitForPageLoad();
    }

    public List<String> getAddedProductNames() {
        return addedProductNames;
    }

    public void deleteAddedProductsFromCart() {
        WebElementUtils.clickElement(driver, cartIcon);
        WebElementUtils.clickElements(driver, deleteButton);
    }
}
