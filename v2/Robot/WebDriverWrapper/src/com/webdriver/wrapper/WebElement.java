package com.webdriver.wrapper;

public interface WebElement {
    void click();

    String getAttribute(String attributeName);

    String getText();

    void setElementValue(String value);
}
