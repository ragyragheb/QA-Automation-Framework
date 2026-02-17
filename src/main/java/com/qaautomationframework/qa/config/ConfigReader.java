package com.qaautomationframework.qa.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH);
            properties.load(fis);
            fis.close();
            logger.info("Configuration properties loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load configuration properties: " + e.getMessage());
            throw new RuntimeException("Configuration file not found at: " + CONFIG_FILE_PATH);
        }
    }

    public static String getProperty(String key) {

        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            logger.info("Using system property: " + key + " = " + systemValue);
            return systemValue;
        }

        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property key not found: " + key);
        }
        return value;
    }

    public static String getBrowser() {
        return getProperty("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless"));
    }

    public static String getAmazonUrl() {
        return getProperty("amazon.url");
    }

    public static String getAmazonEmail() {
        return getProperty("amazon.email");
    }

    public static String getAmazonPassword() {
        return getProperty("amazon.password");
    }

    public static String getApiBaseUrl() {
        return getProperty("api.base.url");
    }

    public static String getAPIKey() {
        return getProperty("api.key");
    }

    public static int getImplicitWait() {
        String wait = getProperty("implicit.wait");
        return wait != null ? Integer.parseInt(wait) : 10;
    }

    public static int getPageLoadTimeout() {
        String timeout = getProperty("page.load.timeout");
        return timeout != null ? Integer.parseInt(timeout) : 30;
    }

    public static int getExplicitWait() {
        String wait = getProperty("explicit.wait");
        return wait != null ? Integer.parseInt(wait) : 20;
    }

    public static int getApiConnectionTimeout() {
        String timeout = getProperty("api.connection.timeout");
        return timeout != null ? Integer.parseInt(timeout) : 10000;
    }

    public static int getApiSocketTimeout() {
        String timeout = getProperty("api.socket.timeout");
        return timeout != null ? Integer.parseInt(timeout) : 10000;
    }

}
