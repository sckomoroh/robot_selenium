package org.robot.ui.activities;

import com.webdriver.wrapper.WebDriver;
import com.webdriver.wrapper.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.robot.ui.WebDriverTaskBase;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesTask extends WebDriverTaskBase<Object, List<ActivityItem>> {
    private final static Logger logger = LogManager.getLogger();

    public ActivitiesTask(WebDriver webDriver) {
        super(webDriver);

        displayName = "Collect activities";
    }

    @Override
    protected boolean runInternal() throws Exception {
        setPhase("Collect activities");

        outputData = new ArrayList<>();

        List<WebElement> webElements = webDriver.getElementsByClassName("rf-project-cover__title js-project-cover-title-link");

        logger.debug(String.format("Found %d element of activities", webElements.size()));

        for (WebElement webElement : webElements) {
            String webReference = webElement.getAttribute("href");
            String name = webElement.getText();

            outputData.add(new ActivityItem(webReference, name));
        }

        return true;
    }
}
