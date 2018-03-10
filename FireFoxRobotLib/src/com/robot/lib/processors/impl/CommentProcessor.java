package com.robot.lib.processors.impl;

import com.robot.lib.processors.BaseProcessor;
import com.robot.lib.processors.ProcessorState;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CommentProcessor extends BaseProcessor {
    private String userWebReference;
    private String activityUrl;
    private String comment;

    public CommentProcessor(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public String getDisplayName() {
        return String.format("Perform comment '%s'", activityUrl);
    }

    public void setProcessorData(Object data) {
        CommentProcessorData processorData = (CommentProcessorData)data;
        userWebReference = processorData.userWebReference;
        activityUrl = processorData.activityWebUrl;
        comment = processorData.comment;
    }

    @Override
    protected void processorAction() {
        fireProcessorStateChanged(ProcessorState.Started, "Comment started");
        fireProcessorMessage("Check is commented");

        if (isExists()) {
            fireProcessorStateChanged(ProcessorState.Skipped, "Already commented");
            return;
        }

        fireProcessorMessage("Do comment");
        doComment();

        fireProcessorStateChanged(ProcessorState.Completed, "Comment completed");
    }

    private boolean isExists() {
        WebElement commentsList = webDriverHelper.findElementByClassName("ul", "js-comments-list comments-list");
        List<WebElement> commentItems = webDriverHelper.findElementsByClassName(commentsList, "a", "rf-avatar js-avatar js-mini-profile");
        for(WebElement commentItem : commentItems)
        {
            String userWebReference = commentItem.getAttribute("href");
            if (this.userWebReference.equals(userWebReference))
            {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings({"StatementWithEmptyBody"})
    private void doComment() {
        WebElement commentInputElement = webDriver.findElement(By.id("comment"));

        WebElement commentSubmitButton = webDriverHelper.findElementByClassName("a", "form-button js-rf-button rf-button js-submit");

        commentInputElement.sendKeys(comment);

        webDriverHelper.clickWebElement(commentSubmitButton);

        while (!isExists()) {
        }
    }
}
