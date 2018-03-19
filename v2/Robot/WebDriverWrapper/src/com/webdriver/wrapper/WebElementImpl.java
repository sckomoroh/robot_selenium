package com.webdriver.wrapper;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebElementImpl implements WebElement {
    private final org.openqa.selenium.WebDriver webDriver;
    private final org.openqa.selenium.WebElement webElement;
    private final JavascriptExecutor javascriptExecutor;
    private final WebDriverWait webDriverWait;

    public WebElementImpl(org.openqa.selenium.WebDriver webDriver, org.openqa.selenium.WebElement webElement, JavascriptExecutor javascriptExecutor, WebDriverWait webDriverWait) {
        this.webDriver = webDriver;
        this.webElement = webElement;
        this.javascriptExecutor = javascriptExecutor;
        this.webDriverWait = webDriverWait;
    }

    @Override
    public void click() {
        javascriptExecutor.executeScript("arguments[0].click(); return true;", webElement);
    }

    @Override
    public String getAttribute(String attributeName) {
        return webElement.getAttribute(attributeName);
    }

    @Override
    public String getText() {
        return webElement.getText();
    }

    @Override
    public void setElementValue(String value) {
        javascriptExecutor.executeScript("arguments[0].value = arguments[1]; return true;", webElement, value);
    }

    @Override
    public boolean isVisible() {
        return webElement.isDisplayed();
    }
}
