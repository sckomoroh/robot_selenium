package com.robot.lib.processors.impl;

import com.robot.lib.processors.BaseProcessor;
import com.robot.lib.processors.ProcessorState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CommentProcessor extends BaseProcessor {
    private static final Logger logger = LogManager.getLogger();

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
            logger.debug(String.format("Comment from '%s' found, skip it", userWebReference));
            fireProcessorStateChanged(ProcessorState.Skipped, "Already commented");
            return;
        }

        logger.info(String.format("Try to past comment '%s'", comment));

        fireProcessorMessage("Do comment");
        doComment();

        logger.debug("Comment completed");

        fireProcessorStateChanged(ProcessorState.Completed, "Comment completed");
    }

    private boolean isExists() {
        WebElement commentsList = webDriverHelper.findElementByClassName("ul", "js-comments-list comments-list");
        if (commentsList == null) {
            logger.warn("Comment list element not found");
        }

        List<WebElement> commentItems = webDriverHelper.findElementsByClassName(commentsList, "a", "rf-avatar js-avatar js-mini-profile");
        if (commentItems == null) {
            logger.warn("Comment items not found");
        }

        for(WebElement commentItem : commentItems) {
            String userWebReference = commentItem.getAttribute("href");
            if (this.userWebReference.equals(userWebReference)) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings({"StatementWithEmptyBody"})
    private void doComment() {
        WebElement commentInputElement = webDriver.findElement(By.id("comment"));
        if (commentInputElement == null) {
            logger.warn("Comment input element not found");
        }

        WebElement commentSubmitButton = webDriverHelper.findElementByClassName("a", "form-button js-rf-button rf-button js-submit");
        if (commentSubmitButton == null) {
            logger.warn("Comment submit button not found");
        }

        commentInputElement.sendKeys(comment);

        webDriverHelper.clickWebElement(commentSubmitButton);

        logger.debug("Wait for comment appears");

        while (!isExists()) {
        }
    }
}
