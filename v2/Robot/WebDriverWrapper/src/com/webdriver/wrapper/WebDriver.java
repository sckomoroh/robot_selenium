package com.webdriver.wrapper;

import java.util.List;

public interface WebDriver {
    void navigate(String url, boolean waitCompletion);

    List<WebElement> getElementsByClassName(String className);

    WebElement getElementById(String elementId);

    List<WebElement> getElementsByClassName(String className, int timeout, int retryCount);

    WebElement getElementById(String elementId, int timeout, int retryCount);

    void waitPageLoadCompleted();

    void quit();

    Object executeJavaScript(String scriptBody);
}
