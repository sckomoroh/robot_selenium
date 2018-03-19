package org.robot.ui.login;

import com.webdriver.wrapper.WebDriver;
import com.webdriver.wrapper.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.robot.ui.WebDriverTaskBase;

import java.util.List;

public class LoginTask extends WebDriverTaskBase<InputLoginTaskData, String> {
    private final static Logger logger = LogManager.getLogger();

    public LoginTask(WebDriver webDriver) {
        super(webDriver);

        displayName = "Login";
    }

    @Override
    protected boolean runInternal() throws Exception {
        navigateToTheLoginPage();
        submitCredentials();
        findUserWebReference();

        return true;
    }

    private void navigateToTheLoginPage() {
        setPhase("Navigate to the login page");

        logger.debug("Fins element by class name 'rf-primary-nav__link js-adobeid-signin qa-adobeid-signin'");

        WebElement webElement = webDriver.getElementsByClassName("rf-primary-nav__link js-adobeid-signin qa-adobeid-signin").iterator().next();

        logger.debug("Click on the element");

        webElement.click();

        logger.debug("Wait until page loaded");

        webDriver.waitPageLoadCompleted();
    }

    private void submitCredentials() {
        setPhase("Submit credentials");

        logger.debug("Get web element for submit");

        WebElement submitElement = webDriver.getElementById("sign_in");
        WebElement usernameElement = webDriver.getElementById("adobeid_username");
        WebElement passwordElement = webDriver.getElementById("adobeid_password");

        logger.debug(String.format("Set credential values. Username: '%s', Password: '%s'", inputData.username, inputData.password));

        usernameElement.setElementValue(inputData.username);
        passwordElement.setElementValue(inputData.password);

        logger.debug("Submit credentials");

        submitElement.click();

        logger.debug("Wait until page completed");

        webDriver.waitPageLoadCompleted();
    }

    private void findUserWebReference() {
        setPhase("Find user WEB reference");

        List<WebElement> targetElements = webDriver.getElementsByClassName("qa-nav-link-image");
        logger.debug(String.format("Found '%d' element", targetElements.size()));

        WebElement targetElement = targetElements.iterator().next();

        outputData = targetElement.getAttribute("href");

        logger.debug(String.format("Find user web reference: '%s'", outputData));
    }
}
