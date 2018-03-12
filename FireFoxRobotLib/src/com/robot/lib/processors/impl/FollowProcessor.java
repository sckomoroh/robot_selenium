package com.robot.lib.processors.impl;

import com.robot.lib.processors.BaseProcessor;
import com.robot.lib.processors.ProcessorState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

public class FollowProcessor extends BaseProcessor {
    private static final Logger logger = LogManager.getLogger();

    private String activityUrl;

    public FollowProcessor(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public String getDisplayName() {
        return String.format("Perform follow '%s'", activityUrl);
    }

    @Override
    protected void processorAction() {
        fireProcessorStateChanged(ProcessorState.Started, "Follow started");
        fireProcessorMessage("Check is followed");

        if (isFollowing()) {
            logger.debug("Already followed, skip it");
            fireProcessorStateChanged(ProcessorState.Skipped, "Already follow");
            return;
        }

        logger.info("Try to follow");

        fireProcessorMessage("Do follow");

        doFollow();

        logger.info("Following completed");

        fireProcessorStateChanged(ProcessorState.Completed, "Follow completed");
    }

    @Override
    public void setProcessorData(Object data) {
        activityUrl = (String)data;
    }

    private void doFollow() {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
        javascriptExecutor.executeScript("document.getElementsByClassName('qa-follow-button-container')[0].click(); return true;");
    }

    private boolean isFollowing() {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
        return (boolean)javascriptExecutor.executeScript("return (document.getElementsByClassName('following').length > 0)");
    }
}
