package org.robot.ui;

import com.webdriver.wrapper.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class WebDriverTaskBase<TInput, TOutput> extends TaskBase<TInput, TOutput> {
    private final static Logger logger = LogManager.getLogger();

    protected final WebDriver webDriver;

    protected WebDriverTaskBase(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public void run() {
        logger.info(String.format("Start TASK: '%s'", getDisplayName()));

        try {
            setState(TaskState.Preparing);

            setPhase("Waiting while page loading completed");

            webDriver.waitPageLoadCompleted();

            setState(TaskState.Running);

            if (runInternal()) {
                setState(TaskState.Completed);
                return;
            }

            setState(TaskState.Skipped);
        } catch (Exception ex) {
            logger.error(String.format("TASK failed: '%s'", getDisplayName()), ex);

            setState(TaskState.Failed);
        }
    }
}
