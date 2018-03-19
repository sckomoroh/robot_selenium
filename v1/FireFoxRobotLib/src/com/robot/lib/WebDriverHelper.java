package com.robot.lib;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class WebDriverHelper {
    private final WebDriver webDriver;
    private final long waitTimeout;

    public WebDriverHelper(WebDriver webDriver)
    {
        this(webDriver, 60);
    }

    public WebDriverHelper(WebDriver webDriver, long waitTimeout)
    {
        this.webDriver = webDriver;
        this.waitTimeout = waitTimeout;
    }

    public WebElement findElementByClassName(String elementName, String className)
    {
        return findElementByClassName(elementName, className, false);
    }

    public WebElement findElementByClassName(String elementName, String className, boolean throwException)
    {
        return findElementByClassName(elementName, className, throwException, false);
    }

    public WebElement findElementByClassName(String elementName, String className, boolean throwException, boolean waitElement)
    {
        WebElement webElement = null;

        String selector = String.format("%s[class='%s']", elementName, className);
        By locator = By.cssSelector(selector);
        try {
            if (waitElement) {
                WebDriverWait webDriveWait = new WebDriverWait(webDriver, waitTimeout);
                webElement = webDriveWait.until(ExpectedConditions.presenceOfElementLocated(locator));
            } else {
                webElement = webDriver.findElement(locator);
            }
        }
        catch (NoSuchElementException ex)
        {
            if (throwException)
            {
                throw ex;
            }
        }

        return webElement;
    }

    public List<WebElement> findElementsByClassName(String elementName, String className, boolean throwException)
    {
        try
        {
            String selector = String.format("%s[class='%s']", elementName, className);
            By locator = By.cssSelector(selector);

            return webDriver.findElements(locator);
        }
        catch (Exception ex)
        {
            if (throwException)
            {
                throw ex;
            }
        }

        return null;
    }

    public List<WebElement> findElementsByClassName(String elementName, String className)
    {
        return findElementsByClassName(elementName, className, false);
    }

    public List<WebElement> findElementsByClassName(WebElement webElement, String elementName, String className, boolean throwException)
    {
        try
        {
            String selector = String.format("%s[class='%s']", elementName, className);
            By locator = By.cssSelector(selector);

            return webElement.findElements(locator);
        }
        catch (Exception ex)
        {
            if (throwException)
            {
                throw ex;
            }
        }

        return null;
    }

    public List<WebElement> findElementsByClassName(WebElement webElement, String elementName, String className)
    {
        return findElementsByClassName(webElement, elementName, className, false);
    }

    public void clickWebElement(WebElement webElement)
    {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor)webDriver;
        javascriptExecutor.executeScript("arguments[0].click();", webElement);

    }

    public void waitPageCompleted() {
        final JavascriptExecutor javaScriptExecutor = (JavascriptExecutor) webDriver;
        WebDriverWait wait = new WebDriverWait(webDriver, 60);
        wait.until((ExpectedCondition<Boolean>) webDriver -> {
            Object scriptResult = javaScriptExecutor.executeScript("return (document.readyState == 'complete' && (typeof jQuery != 'undefined' && jQuery.active == 0))");
            return (boolean)scriptResult;
        });
    }
}
