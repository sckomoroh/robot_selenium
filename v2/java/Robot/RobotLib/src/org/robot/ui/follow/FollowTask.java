package org.robot.ui.follow;

import com.webdriver.wrapper.WebDriver;
import com.webdriver.wrapper.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.robot.ui.WebDriverTaskBase;

import java.util.List;

public class FollowTask extends WebDriverTaskBase<String, Object> {
    private final static Logger logger = LogManager.getLogger();

    public FollowTask(WebDriver webDriver, String name) {
        super(webDriver);
        displayName = String.format("Following '%s'", name);
    }

    @Override
    protected boolean runInternal() throws Exception {
        if (isFollowing()) {
            logger.info(String.format("Activity '%s' already followed", getDisplayName()));

            setPhase("Already followed");

            return false;
        }

        doFollow();

        waitFollow();

        return true;
    }

    private boolean isFollowing() {
        setPhase("Check for following");

        logger.debug("Check for the following");

        boolean isFollowing = false;
        int retryCount = 0;
        do {
            List<WebElement> targetElements = (List<WebElement>) webDriver.executeJavaScript("return document.getElementsByClassName('following');");

            logger.debug(String.format("Found %d that indicated to following", targetElements.size()));

            isFollowing = targetElements.size() > 0;
            if (isFollowing == false) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                }
            }
        }
        while(retryCount++ < 5 && isFollowing == false);

        return isFollowing;
    }

    private void doFollow() {
        setPhase("Following");

        logger.debug("Get follow button");

        WebElement targetElement = webDriver.getElementsByClassName("qa-follow-button-container").iterator().next();;

        logger.debug("Click on the appreciate button");

        targetElement.click();

        logger.info("Following completed");
    }


    private void waitFollow() {
        setPhase("Wait following");

        logger.debug("Wait for following");

        while (!isFollowing()) { }

        logger.info("Following completed");
    }
}
