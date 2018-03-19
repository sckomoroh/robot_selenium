package com.webdriver.wrapper;

import com.google.common.base.Function;
import org.openqa.selenium.JavascriptExecutor;

public class PageCompletedWaitCondition implements Function<org.openqa.selenium.WebDriver, Boolean> {
    private final JavascriptExecutor javaScriptExecutor;

    public PageCompletedWaitCondition(JavascriptExecutor javaScriptExecutor) {
        this.javaScriptExecutor = javaScriptExecutor;
    }

    @Override
    public Boolean apply(org.openqa.selenium.WebDriver webDriver) {
        String jQueryType = (String)javaScriptExecutor.executeScript("return typeof jQuery");
        if (jQueryType.equals("undefined")) {
            return false;
        }

        String documentState = (String)javaScriptExecutor.executeScript("return document.readyState");
        if (!documentState.equals("complete")) {
            return false;
        }

        long jQueryActive = (long)javaScriptExecutor.executeScript("return jQuery.active");
        if (jQueryActive != 0) {
            return false;
        }

        return true;
    }
}
