package com.robot.lib.processors.impl;

import com.robot.lib.processors.BaseProcessor;
import com.robot.lib.processors.ProcessorState;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

@SuppressWarnings("SpellCheckingInspection")
public class LoginProcessor extends BaseProcessor {
    private String userName;
    private String password;

    private String userWebReference;

    public LoginProcessor(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void setProcessorData(Object data) {
        LoginProcessorData credentials = (LoginProcessorData)data;

        userName = credentials.Username;
        password = credentials.Password;
    }

    @Override
    public String getDisplayName() {
        return "Login";
    }

    @Override
    public Object getProcessorData()
    {
        return userWebReference;
    }

    @Override
    protected void processorAction() {
        fireProcessorStateChanged(ProcessorState.Started, "Login started");

        WebElement loginElement = webDriverHelper.findElementByClassName(
                "a",
                "rf-primary-nav__link js-adobeid-signin qa-adobeid-signin");

        if (loginElement == null) {
            fireProcessorStateChanged(ProcessorState.Failed, "Login element not found");
            return;
        }

        fireProcessorMessage("Navigate to the login page");
        String oldUrl = webDriver.getCurrentUrl().toUpperCase();

        webDriverHelper.clickWebElement(loginElement);

        String newUrl;

        do {
            newUrl = webDriver.getCurrentUrl().toUpperCase();
        }
        while (oldUrl.equals(newUrl));

        webDriverHelper.waitPageCompleted();

        WebElement submitElement = webDriver.findElement(By.id("sign_in"));
        WebElement usernameElement = webDriver.findElement(By.id("adobeid_username"));
        WebElement passwordElement = webDriver.findElement(By.id("adobeid_password"));

        usernameElement.sendKeys(userName);
        waitForPasswordValueInput(passwordElement);

        fireProcessorMessage("Submit login");
        webDriverHelper.clickWebElement(submitElement);
        webDriverHelper.waitPageCompleted();

        fireProcessorMessage("Get user WEB reference");
        WebElement userElement = webDriverHelper.findElementByClassName("a", "qa-nav-link-image", true, true);
        userWebReference = userElement.getAttribute("href");

        fireProcessorStateChanged(ProcessorState.Completed, String.format("Completed: '%s'", userWebReference));
    }

    private void waitForPasswordValueInput(WebElement passwordElement) {
        // TODO: Check for stability
        final JavascriptExecutor javaScriptExecutor = (JavascriptExecutor) webDriver;
        WebDriverWait wait = new WebDriverWait(webDriver, 60);
        wait.until((ExpectedCondition<Boolean>) webDriver -> {
            passwordElement.sendKeys(password);
            Object scriptResult = javaScriptExecutor.executeScript("return document.getElementById('adobeid_password').value;");
            String elementPasswordValue = (String)scriptResult;

            return password.equals(elementPasswordValue);
        });
    }
}
