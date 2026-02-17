package com.qaautomationframework.qa.stepdefinitions;

import com.qaautomationframework.qa.api.endpoints.ApiEndpoints;
import com.qaautomationframework.qa.api.pojos.User;
import com.qaautomationframework.qa.api.pojos.UserResponse;
import com.qaautomationframework.qa.utils.ApiUtils;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserApiStepDefinitions {

    private static final Logger logger = LogManager.getLogger(UserApiStepDefinitions.class);

    private User testUser;
    private Response apiResponse;
    private String requestBody;

    @Before
    public void setUp() {
        logger.info("========== Starting API Test Scenario ==========");
    }

    @Given("API base URL is configured")
    public void apiBaseUrlIsConfigured() {
        logger.info("API Base URL is configured and ready");
    }

    @Given("Enter user data with Id {string}, name {string} and job {string} and age {int}")
    public void enterUserNameAndJobAndAge(String Id, String name, String job, int age) {
        testUser = new User();
        testUser.setId(Id);
        testUser.setName(name);
        testUser.setJob(job);
        testUser.setAge(age);
        logger.info("Test user created: " + testUser);
    }

    @When("Send a POST request to create the user")
    public void sendAPostRequestToCreateTheUser() {
        if (requestBody != null && requestBody.equals("{}")) {
            apiResponse = ApiUtils.post(ApiEndpoints.CREATE_USER, requestBody);
        } else {
            apiResponse = ApiUtils.post(ApiEndpoints.CREATE_USER, testUser);
        }
    }

    @When("Send a GET request to retrieve user with ID {string}")
    public void sendAGetRequestToRetrieveUserWithId(String userId) {
        apiResponse = ApiUtils.get(ApiEndpoints.GET_USER, userId);
    }

    @When("Send a PUT request to update user with ID {string}")
    public void sendAPutRequestToUpdateUserWithId(String userId) {
        apiResponse = ApiUtils.put(ApiEndpoints.UPDATE_USER, userId, testUser);
    }

    @Then("Response status code should be {int}")
    public void responseStatusCodeShouldBe(int expectedStatusCode) {
        ApiUtils.validateStatusCode(apiResponse, expectedStatusCode);
    }

    @Then("Response should contain user ID")
    public void responseShouldContainUserId() {
        User createdUser = apiResponse.as(User.class);
        assert createdUser.getId() != null : "User ID should not be null";
    }

    @Then("Response should contain name {string}")
    public void responseShouldContainName(String expectedName) {
        User user = apiResponse.as(User.class);
        assert user.getName().equals(expectedName) : "Expected name: " + expectedName + " but got: " + user.getName();
    }

    @Then("Response should contain job {string}")
    public void responseShouldContainJob(String expectedJob) {
        User user = apiResponse.as(User.class);
        assert user.getJob().equals(expectedJob) : "Expected job: " + expectedJob + " but got: " + user.getJob();
    }

    @Then("Response should contain createdAt timestamp")
    public void responseShouldContainCreatedAtTimestamp() {
        User user = apiResponse.as(User.class);
        assert user.getCreatedAt() != null : "CreatedAt timestamp should not be null";
    }

    @Then("Response should contain updatedAt timestamp")
    public void responseShouldContainUpdatedAtTimestamp() {
        User user = apiResponse.as(User.class);
        assert user.getUpdatedAt() != null : "UpdatedAt timestamp should not be null";
    }

    @Then("Response should contain user data")
    public void responseShouldContainUserData() {
        UserResponse userResponse = apiResponse.as(UserResponse.class);
        UserResponse.UserData userData = userResponse.getData();
        assert userData != null : "User data should not be null";
    }

    @Then("User should have first name and last name")
    public void userShouldHaveFirstNameAndLastName() {
        UserResponse userResponse = apiResponse.as(UserResponse.class);
        UserResponse.UserData userData = userResponse.getData();
        assert userData.getFirstName() != null : "First name should not be null";
        assert userData.getLastName() != null : "Last name should not be null";
        logger.info("First name: " + userData.getFirstName() + ", Last name: " + userData.getLastName());
    }

    @Given("Enter empty request body")
    public void enterEmptyRequestBody() {
        requestBody = "{}";
        logger.info("Set empty request body");
    }

    @When("Send a DELETE request for user with ID {string}")
    public void sendADeleteRequestForUserWithId(String userId) {
        apiResponse = ApiUtils.delete(ApiEndpoints.DELETE_USER, userId);
    }

    @Then("Response body should be empty JSON object")
    public void responseBodyShouldBeEmptyJsonObject() {
        String body = apiResponse.getBody().asString();
        assert body.trim().equals("{}") : "Expected empty JSON object but got: " + body;
        logger.info("Empty JSON response body validated");
    }

}
