package org.robot.ui.webdriver;

import com.webdriver.wrapper.CreateWebDriverOptions;
import com.webdriver.wrapper.WebDriver;
import com.webdriver.wrapper.WebDriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.robot.ui.TaskBase;
import org.robot.ui.TaskState;

public class CreateWebDriverTask extends TaskBase<InputCreateWebDriverTaskData, WebDriver> {
    private final static Logger logger = LogManager.getLogger();

    public CreateWebDriverTask() {
        displayName = "Create WEB driver";
    }

    @Override
    protected boolean runInternal() throws Exception {
        setState(TaskState.Running);
        setPhase("Creating WEB driver");

        CreateWebDriverOptions options = new CreateWebDriverOptions();
        options.driverLocation = inputData.driverLocation;
        options.driverType = inputData.driverType;

        logger.debug(String.format("Creating WEB driver. Options: Type: '%s' Location: '%s'", options.driverType, options.driverLocation));

        outputData = WebDriverFactory.createWebDriver(options);

        String phase = String.format("Navigate to '%s'", inputData.startUrl);
        setPhase(phase);

        logger.debug(String.format("Navigate to the Location: '%s'", inputData.startUrl));
        outputData.navigate(inputData.startUrl, true);

        return true;
    }
}
