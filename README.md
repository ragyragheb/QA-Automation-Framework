# QA Automation Framework

A comprehensive test automation framework featuring Selenium WebDriver for UI testing and RestAssured for API testing, built with Java, Maven, Cucumber BDD, and TestNG.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Test Scenarios](#test-scenarios)
- [Reports](#reports)
- [Troubleshooting](#troubleshooting)

## Overview

This framework automates:
1. **Selenium Tests**: E2E testing of Amazon.eg video games purchase flow
2. **API Tests**: RESTful API testing using reqres.in endpoints

Both test suites are written in **Cucumber BDD** style with Gherkin feature files, step definitions, and dedicated TestNG-Cucumber runners.

## Features

- **Cucumber BDD** with Gherkin feature files for readable test scenarios
- **Page Object Model (POM)** design pattern for Selenium tests
- **RestAssured** for API testing with POJO models
- **TestNG** as the underlying test runner (via Cucumber-TestNG integration)
- **WebDriverManager** for automatic browser driver management
- **Allure Reports** for comprehensive HTML test reports (single-file generation)
- **Log4j2** for console and file logging
- **Maven** for dependency management and build lifecycle
- **Configuration management** via properties files with system property overrides
- **Thread-safe WebDriver** via `ThreadLocal` for parallel execution support
- **Retry mechanisms** for handling stale element exceptions in Selenium

## Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming Language |
| Selenium | 4.15.0 | Web UI Automation |
| RestAssured | 5.4.0 | API Testing |
| Cucumber | 7.14.0 | BDD Framework |
| TestNG | 7.8.0 | Test Runner |
| Maven | 3.6+ | Build Tool |
| WebDriverManager | 5.6.2 | Browser Driver Management |
| Allure | 2.25.0 | Test Reporting |
| Log4j2 | 2.21.1 | Logging |
| Jackson | 2.15.3 | JSON Serialization |

## Project Structure

```
QA-Automation-Framework/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/qaautomationframework/qa/
│   │           ├── pages/                  # Page Object Models
│   │           │   ├── BasePage.java
│   │           │   ├── AmazonLoginPage.java
│   │           │   ├── AmazonVideoGamesPage.java
│   │           │   ├── AmazonCartPage.java
│   │           │   └── AmazonCheckoutPage.java
│   │           ├── utils/                  # Utility classes
│   │           │   ├── DriverManager.java
│   │           │   ├── WebElementUtils.java
│   │           │   └── ApiUtils.java
│   │           ├── config/                 # Configuration
│   │           │   └── ConfigReader.java
│   │           └── api/
│   │               ├── pojos/             # API POJOs
│   │               │   ├── User.java
│   │               │   └── UserResponse.java
│   │               └── endpoints/         # API Endpoints
│   │                   └── ApiEndpoints.java
│   └── test/
│       ├── java/
│       │   └── com/qaautomationframework/qa/
│       │       ├── runners/               # Cucumber-TestNG Runners
│       │       │   ├── ApiRunner.java
│       │       │   └── GUIRunner.java
│       │       └── stepdefinitions/        # Cucumber Step Definitions
│       │           ├── UserApiStepDefinitions.java
│       │           └── VideoGamesStepDefinitions.java
│       └── resources/
│           ├── features/                  # Gherkin Feature Files
│           │   ├── UserApi.feature
│           │   └── AmazonVideoGames.feature
│           ├── config.properties          # Configuration file
│           ├── allure.properties          # Allure output config
│           └── log4j2.xml                # Logging config
├── testng.xml                            # TestNG suite config
├── pom.xml                              # Maven configuration
├── .gitignore                           # Git ignore rules
└── README.md                            # This file
```

## Prerequisites

Before you begin, ensure you have the following installed:

1. **Java Development Kit (JDK) 17 or higher**
   ```bash
   java -version
   ```

2. **Apache Maven 3.6 or higher**
   ```bash
   mvn -version
   ```

3. **Git** (optional, for cloning)
   ```bash
   git --version
   ```

4. **Chrome Browser** (default; Firefox and Edge also supported)

## Installation

### Step 1: Clone or Download the Project

**Option A: Clone with Git**
```bash
git clone <repository-url>
cd QA-Automation-Framework
```

**Option B: Download ZIP**
- Download and extract the project ZIP file
- Navigate to the project directory

### Step 2: Install Dependencies

```bash
mvn clean install -DskipTests
```

### Step 3: Verify Installation

```bash
mvn compile
```

Expected output: `BUILD SUCCESS`

## Configuration

Edit `src/test/resources/config.properties`:

```properties
# Browser Configuration
browser=chrome
headless=false
implicit.wait=10
page.load.timeout=30
explicit.wait=20

# Amazon Configuration
amazon.url=https://www.amazon.eg/-/en/
amazon.email=your-email-or-phone
amazon.password=your-password

# API Configuration
api.base.url=https://reqres.in
api.key=your-api-key
api.connection.timeout=10000
api.socket.timeout=10000
```

**Notes:**
- Replace Amazon credentials with your actual Amazon.eg account details
- Set `headless=true` for headless browser execution (or pass `-Dheadless=true` on the command line)
- System properties (`-Dkey=value`) override values in `config.properties`

## Running Tests

### Run All Tests (via Maven Failsafe)

```bash
mvn clean verify
```

This executes both `ApiRunner` and `GUIRunner` through the Maven Failsafe plugin.

### Run API Tests Only

```bash
mvn clean verify -Dit.test=ApiRunner
```

### Run Selenium (GUI) Tests Only

```bash
mvn clean verify -Dit.test=GUIRunner
```

### Run in Headless Mode

```bash
mvn clean verify -Dheadless=true
```

### Run with a Specific Browser

```bash
mvn clean verify -Dbrowser=firefox
```

Supported browsers: `chrome`, `firefox`, `edge`

## Test Scenarios

### Selenium Test: Amazon Video Games Purchase Flow

**Feature file:** `src/test/resources/features/AmazonVideoGames.feature`
**Runner:** `GUIRunner.java` (tag: `@Amazon`)

**Scenario: Search and filter video games**
1. Open Amazon.eg and log in
2. Navigate to All menu > Video Games > All Video Games
3. Apply Free Shipping filter
4. Apply New Condition filter
5. Sort by Price: High to Low
6. Add products priced below 15,000 EGP to cart — **currently limited to the first 5 items** (configurable via `addProductsBelow15K(maxItems)`)
7. Collect the cart subtotal item by item from the cart page
8. Proceed to checkout, add a shipping address, and select Buy Now Pay Later with Valu
9. Verify that the order total on the checkout page matches the cart subtotal plus shipping fees
10. **Delete all added products from cart at the end of each test run** (cleanup step ensures the cart is empty for the next run)

### API Tests: User CRUD Operations

**Feature file:** `src/test/resources/features/UserApi.feature`
**Runner:** `ApiRunner.java` (tag: `@api`)
**Base URL:** https://reqres.in

| Scenario | Method | Endpoint | Expected Status | Description |
|----------|--------|----------|-----------------|-------------|
| Create a new user | POST | /api/users | 201 | Creates user with name, job, age; verifies id, name, job, createdAt |
| Retrieve an existing user | GET | /api/users/2 | 200 | Retrieves user by ID; verifies first and last name in response |
| Update user details | PUT | /api/users/2 | 200 | Updates user info; verifies name, job, updatedAt |
| Retrieve a non-existent user | GET | /api/users/999 | 404 | Verifies empty JSON body for missing user |
| Create user with empty body | POST | /api/users | 201 | Sends `{}` body; verifies createdAt is returned |
| Delete a user | DELETE | /api/users/2 | 204 | Deletes user; verifies empty response |

## Reports

### Allure Reports
- **Results directory:** `allure-results/`
- **Single-file report:** `allure-report/` (auto-generated after test phase)
- Interactive HTML report with test steps, request/response details, and timeline

**Generate and serve the report manually:**
```bash
mvn allure:serve
```

### Cucumber Reports
- **API HTML report:** `target/cucumber-reports/api-cucumber.html`
- **API JSON report:** `target/cucumber-reports/api-cucumber.json`
- **GUI HTML report:** `target/cucumber-reports/cucumber.html`
- **GUI JSON report:** `target/cucumber-reports/cucumber.json`

### Log Files
- **Console:** Real-time logs during test execution
- **File:** `test-output/All Logs.log` with timestamps

## Troubleshooting

### 1. Build Failure
```
Error: Failed to execute goal
```
**Solution:**
```bash
mvn clean install -U -DskipTests
```

### 2. Driver Not Found
```
Error: WebDriver executable not found
```
**Solution:**
- WebDriverManager handles driver downloads automatically
- Ensure you have an internet connection
- Check firewall settings

### 3. Element Not Found / Stale Element
```
NoSuchElementException or StaleElementReferenceException
```
**Solution:**
- The framework has a built-in retry mechanism (up to 3 retries)
- Increase wait times in `config.properties`:
  ```properties
  explicit.wait=30
  ```

### 4. Login Failures
**Solution:**
- Verify credentials in `config.properties`
- Ensure the Amazon.eg account is not locked
- Check for CAPTCHA requirements
- Try logging in manually first to verify the account

### 5. Maven Dependency Issues
```
Could not resolve dependencies
```
**Solution:**
```bash
mvn dependency:purge-local-repository
mvn clean install
```

## Security Notes
- Consider uncommenting the `config.properties` entry in `.gitignore` before sharing the repository
