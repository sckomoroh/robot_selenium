package com.robot.lib.processors.impl;

import com.robot.lib.processors.BaseProcessor;
import com.robot.lib.processors.ProcessorState;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@SuppressWarnings("SpellCheckingInspection")
public class AppreciateProcessor extends BaseProcessor {
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
            fireProcessorStateChanged(ProcessorState.Skipped, "Already appreciated");
            return;
        }

        fireProcessorMessage("Do appreciate");
        doAppreciate();

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
            System.out.println("Could not find the appreciate button. Something goes wrong");
        } else {
            System.out.println("Appreciating activity");
            webDriverHelper.clickWebElement(appreciateElement);
        }
    }
}
