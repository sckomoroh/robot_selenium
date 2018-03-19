package com.webdriver.wrapper;

import com.webdriver.wrapper.exceptions.InvalidWebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverFactory {
    public static WebDriver createWebDriver(CreateWebDriverOptions options)
            throws InvalidWebDriverException {
        switch (options.driverType) {
            case Chrome:
                return createChromeWebDriver(options.driverLocation);
            case Firefox:
                return createFirefoxWebDriver(options.driverLocation);
        }

        throw new InvalidWebDriverException("Invalid type of web driver was specified");
    }

    private static WebDriver createChromeWebDriver(String driverLocation) {
        System.setProperty("webdriver.chrome.driver", driverLocation);

        org.openqa.selenium.WebDriver webDriver = new ChromeDriver();

        return new WebDriverImpl(webDriver, 60);
    }

    private static WebDriver createFirefoxWebDriver(String driverLocation) {
        System.setProperty("webdriver.gecko.driver", driverLocation);

        org.openqa.selenium.WebDriver webDriver = new FirefoxDriver();

        return new WebDriverImpl(webDriver, 60);
    }
}
