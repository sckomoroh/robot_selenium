package org.robot.ui.appreciate;

import com.webdriver.wrapper.WebDriver;
import com.webdriver.wrapper.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.robot.ui.WebDriverTaskBase;

import java.util.List;

public class AppreciateTask extends WebDriverTaskBase {
    private final static Logger logger = LogManager.getLogger();

    public AppreciateTask(WebDriver webDriver, String name) {
        super(webDriver);
        displayName = String.format("Appreciation '%s'", name);
    }

    @Override
    protected boolean runInternal() throws Exception {
        if (isAppreciated()) {
            logger.info(String.format("Activity '%s' already appreciated", getDisplayName()));

            setPhase("Already followed");
            return false;
        }

        doAppreciate();

        waitAppreciation();

        return true;
    }

    private boolean isAppreciated() {
        setPhase("Check for the appreciation");

        logger.debug("Check for the appreciation");

        boolean isAppreciate = false;
        int retryCount = 0;

        do {
            List<WebElement> targetElements = webDriver.getElementsByClassName("appreciation-button js-appreciate js-adobe-analytics can-unappreciate thanks");

            logger.debug(String.format("Found %d that indicated to appreciating", targetElements.size()));

            isAppreciate = targetElements.size() > 0;
            if (isAppreciate == false) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                }
            }
        }
        while (retryCount++ < 5 && isAppreciate == false);

        return isAppreciate;
    }

    private void doAppreciate() {
        setPhase("Appreciating");

        logger.debug("Get appreciate button");

        WebElement targetElement = webDriver.getElementsByClassName("appreciation-button js-appreciate js-adobe-analytics can-unappreciate").iterator().next();

        logger.debug("Click on the appreciate button");

        targetElement.click();

        logger.info("Appreciation completed");
    }

    private void waitAppreciation() {
        setPhase("Wait appreciating");

        logger.debug("Wait for appreciation");

        while (!isAppreciated()) { }

        logger.info("Appreciate completed");
    }
}
