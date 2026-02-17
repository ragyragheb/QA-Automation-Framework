package com.qaautomationframework.qa.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features/UserApi.feature",
        glue = {"com.qaautomationframework.qa.stepdefinitions"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/api-cucumber.html",
                "json:target/cucumber-reports/api-cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@api",
        monochrome = true,
        dryRun = false)
public class ApiRunner extends AbstractTestNGCucumberTests {
}
