package com.qaautomationframework.qa.utils;

import com.qaautomationframework.qa.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketTimeoutException;

public class ApiUtils {
    private static final Logger logger = LogManager.getLogger(ApiUtils.class);

    static {
        RestAssured.baseURI = ConfigReader.getApiBaseUrl();
        logger.info("RestAssured base URI set to: " + RestAssured.baseURI);
    }

    public static RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .header("x-api-key", ConfigReader.getAPIKey())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .config(RestAssuredConfig.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", ConfigReader.getApiConnectionTimeout())
                                .setParam("http.socket.timeout", ConfigReader.getApiSocketTimeout())))
                .log().all();
    }

    public static Response post(String endpoint, Object body) {
        try {
            logger.info("Sending POST request to: " + endpoint);
            Response response = getRequestSpec()
                    .body(body)
                    .when()
                    .post(endpoint)
                    .then()
                    .log().all()
                    .extract().response();

            logger.info("POST Response Status: " + response.getStatusCode());
            return response;
        } catch (Exception e) {
            if (isConnectionOrTimeoutException(e)) {
                logger.error("POST request connection/timeout failure to: " + endpoint + " - " + e.getMessage());
                throw new RuntimeException("Connection failed for POST " + endpoint, e);
            }
            logger.error("POST request failed: " + e.getMessage());
            throw new RuntimeException("API POST request failed", e);
        }
    }

    public static Response get(String endpoint) {
        try {
            logger.info("Sending GET request to: " + endpoint);
            Response response = getRequestSpec()
                    .when()
                    .get(endpoint)
                    .then()
                    .log().all()
                    .extract().response();

            logger.info("GET Response Status: " + response.getStatusCode());
            return response;
        } catch (Exception e) {
            if (isConnectionOrTimeoutException(e)) {
                logger.error("GET request connection/timeout failure to: " + endpoint + " - " + e.getMessage());
                throw new RuntimeException("Connection failed for GET " + endpoint, e);
            }
            logger.error("GET request failed: " + e.getMessage());
            throw new RuntimeException("API GET request failed", e);
        }
    }

    public static Response get(String endpoint, String pathParam) {
        try {
            logger.info("Sending GET request to: " + endpoint + " with param: " + pathParam);
            Response response = getRequestSpec()
                    .pathParam("id", pathParam)
                    .when()
                    .get(endpoint)
                    .then()
                    .log().all()
                    .extract().response();

            logger.info("GET Response Status: " + response.getStatusCode());
            return response;
        } catch (Exception e) {
            if (isConnectionOrTimeoutException(e)) {
                logger.error("GET request connection/timeout failure to: " + endpoint + " - " + e.getMessage());
                throw new RuntimeException("Connection failed for GET " + endpoint, e);
            }
            logger.error("GET request failed: " + e.getMessage());
            throw new RuntimeException("API GET request failed", e);
        }
    }

    public static Response put(String endpoint, String pathParam, Object body) {
        try {
            logger.info("Sending PUT request to: " + endpoint + " with param: " + pathParam);
            Response response = getRequestSpec()
                    .pathParam("id", pathParam)
                    .body(body)
                    .when()
                    .put(endpoint)
                    .then()
                    .log().all()
                    .extract().response();

            logger.info("PUT Response Status: " + response.getStatusCode());
            return response;
        } catch (Exception e) {
            if (isConnectionOrTimeoutException(e)) {
                logger.error("PUT request connection/timeout failure to: " + endpoint + " - " + e.getMessage());
                throw new RuntimeException("Connection failed for PUT " + endpoint, e);
            }
            logger.error("PUT request failed: " + e.getMessage());
            throw new RuntimeException("API PUT request failed", e);
        }
    }

    public static Response delete(String endpoint, String pathParam) {
        try {
            logger.info("Sending DELETE request to: " + endpoint + " with param: " + pathParam);
            Response response = getRequestSpec()
                    .pathParam("id", pathParam)
                    .when()
                    .delete(endpoint)
                    .then()
                    .log().all()
                    .extract().response();

            logger.info("DELETE Response Status: " + response.getStatusCode());
            return response;
        } catch (Exception e) {
            if (isConnectionOrTimeoutException(e)) {
                logger.error("DELETE request connection/timeout failure to: " + endpoint + " - " + e.getMessage());
                throw new RuntimeException("Connection failed for DELETE " + endpoint, e);
            }
            logger.error("DELETE request failed: " + e.getMessage());
            throw new RuntimeException("API DELETE request failed", e);
        }
    }

    public static void validateStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        if (actualStatusCode != expectedStatusCode) {
            String body = response.getBody().asString();
            logger.error("Status code mismatch! Expected: " + expectedStatusCode +
                    ", Actual: " + actualStatusCode);
            throw new AssertionError("Expected status code: " + expectedStatusCode +
                    " but got: " + actualStatusCode);
        }
        logger.info("Status code validated: " + actualStatusCode);
    }

    private static boolean isConnectionOrTimeoutException(Throwable e) {
        while (e != null) {
            if (e instanceof ConnectTimeoutException
                    || e instanceof SocketTimeoutException
                    || e instanceof HttpHostConnectException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }
}
