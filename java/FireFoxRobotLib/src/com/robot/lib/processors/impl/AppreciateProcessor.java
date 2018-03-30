package com.robot.lib.processors.impl;

import com.robot.lib.processors.BaseProcessor;
import com.robot.lib.processors.ProcessorState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@SuppressWarnings("SpellCheckingInspection")
public class AppreciateProcessor extends BaseProcessor {
    private static final Logger logger = LogManager.getLogger();

    private String activityUrl;

    public AppreciateProcessor(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public String getDisplayName() {
        return String.format("Perform appreciate '%s'", activityUrl);
    }

    @Override
    protected void processorAction() {
        fireProcessorStateChanged(ProcessorState.Started, "Appreciate started");
        fireProcessorMessage("Check is appreciated");

        if (isAppreciated()) {
            logger.info(String.format("Link already appreciated, skip it"));
            fireProcessorStateChanged(ProcessorState.Skipped, "Already appreciated");
            return;
        }

        logger.debug("Try to do appreciate");

        fireProcessorMessage("Do appreciate");

        doAppreciate();

        logger.debug("Appreciation completed");

        fireProcessorStateChanged(ProcessorState.Completed, "Appreciate completed");
    }

    @Override
    public void setProcessorData(Object data) {
        activityUrl = (String)data;
    }

    private boolean isAppreciated() {
        return webDriverHelper.findElementByClassName("div", "appreciation-button js-appreciate js-adobe-analytics can-unappreciate thanks") != null;
    }

    private void doAppreciate() {
        WebElement appreciateElement = webDriverHelper.findElementByClassName("div", "appreciation-button js-appreciate js-adobe-analytics can-unappreciate");
        if (appreciateElement == null) {
            logger.warn("Element for appreciation click not found");
        }

        webDriverHelper.clickWebElement(appreciateElement);
    }
}
