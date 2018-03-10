package com.robot.lib.processors.impl;

import com.robot.lib.processors.BaseProcessor;
import com.robot.lib.processors.ProcessorState;
import org.openqa.selenium.*;

public class FollowProcessor extends BaseProcessor {
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
            fireProcessorStateChanged(ProcessorState.Skipped, "Already follow");
            return;
        }

        fireProcessorMessage("Do follow");

        doFollow();

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
