package com.webdriver.wrapper;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

class WebDriverImpl implements WebDriver {
    private final org.openqa.selenium.WebDriver webDriver;
    private final JavascriptExecutor javascriptExecutor;
    private final WebDriverWait webDriverWait;

    WebDriverImpl(org.openqa.selenium.WebDriver webDriver, int waitTimeoutInSeconds) {
        this.webDriver = webDriver;
        this.webDriverWait = new WebDriverWait(this.webDriver, waitTimeoutInSeconds);

        this.javascriptExecutor = (JavascriptExecutor)this.webDriver;
    }

    @Override
    public void navigate(String url, boolean waitCompletion) {
        webDriver.get(url);

        if (waitCompletion) {
            waitPageLoadCompleted();
        }
    }

    @Override
    public List<WebElement> getElementsByClassName(String className) {
        List<WebElement> resultElements = new ArrayList<>();

        String javascriptBody = String.format("return document.getElementsByClassName('%s');", className);
        List<org.openqa.selenium.WebElement> webElements = (List<org.openqa.selenium.WebElement>)javascriptExecutor.executeScript(javascriptBody);
        for (org.openqa.selenium.WebElement webElement : webElements) {
            resultElements.add(new WebElementImpl(webDriver, webElement, javascriptExecutor, webDriverWait));
        }

        return resultElements;
    }

    @Override
    public WebElement getElementById(String elementId) {
        String javascriptBody = String.format("return document.getElementById('%s');", elementId);
        org.openqa.selenium.WebElement webElement = (org.openqa.selenium.WebElement)javascriptExecutor.executeScript(javascriptBody);

        return new WebElementImpl(webDriver, webElement, javascriptExecutor, webDriverWait);
    }

    @Override
    public void waitPageLoadCompleted() {
        webDriverWait.until(new PageCompletedWaitCondition(javascriptExecutor));
    }

    @Override
    public void quit() {
        webDriver.quit();
    }

    public Object executeJavaScript(String scriptBody) {
        return javascriptExecutor.executeScript(scriptBody);
    }
}
