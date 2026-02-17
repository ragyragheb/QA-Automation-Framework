@api
Feature: Reqres API CRUD Operations

  Background:
    Given API base URL is configured

  @api
  Scenario: Create a new user
    Given Enter user data with Id "2", name "Ragy Ragheb" and job "Senior QA Automation Engineer" and age 27
    When Send a POST request to create the user
    Then Response status code should be 201
    And Response should contain user ID
    And Response should contain name "Ragy Ragheb"
    And Response should contain job "Senior QA Automation Engineer"
    And Response should contain createdAt timestamp

  @api
  Scenario: Retrieve an existing user
    When Send a GET request to retrieve user with ID "2"
    Then Response status code should be 200
    And Response should contain user data
    And User should have first name and last name

  @api
  Scenario: Update user details
    Given Enter user data with Id "2", name "Ragy Ragheb" and job "Senior QA Automation Engineer" and age 27
    When Send a PUT request to update user with ID "2"
    Then Response status code should be 200
    And Response should contain name "Ragy Ragheb"
    And Response should contain job "Senior QA Automation Engineer"
    And Response should contain updatedAt timestamp

  @api @negative
  Scenario: Retrieve a non-existent user returns 404
    When Send a GET request to retrieve user with ID "999"
    Then Response status code should be 404
    And Response body should be empty JSON object

  @api @negative
  Scenario: Create user with empty body
    Given Enter empty request body
    When Send a POST request to create the user
    Then Response status code should be 201
    And Response should contain createdAt timestamp

  @api @negative
  Scenario: Delete a user
    When Send a DELETE request for user with ID "2"
    Then Response status code should be 204
