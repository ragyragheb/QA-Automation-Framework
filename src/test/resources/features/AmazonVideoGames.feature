@Amazon
Feature: Amazon Video Games Shopping

  Background:
    Given User opens Amazon website

  @Amazon
  Scenario: Search and filter video games
    When User logs in to Amazon
    And User navigates to Video Games category
    And User applies Free Shipping filter
    And User applies New Condition filter
    And User sorts by Price High to Low
    And User adds products below 15K to cart
    And User validates items total against cart total
    Then User should delete added products from cart
