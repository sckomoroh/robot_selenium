package com.robot.lib.processors;

import com.robot.lib.WebDriverHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseProcessor implements RobotProcessor {
    private static final Logger logger = LogManager.getLogger();

    private final List<ProcessorListener> processorListeners;

    private ProcessorState processorState;

    protected final WebDriver webDriver;
    protected final WebDriverHelper webDriverHelper;

    protected BaseProcessor(WebDriver webDriver) {
        this.webDriver = webDriver;
        webDriverHelper = new WebDriverHelper(this.webDriver);

        processorListeners = new ArrayList<>();

        processorState = ProcessorState.Pending;
    }

    public Object getProcessorData() {
        return null;
    }

    public void setProcessorData(Object data)
    {
    }

    public ProcessorState getState() {
        return processorState;
    }

    public void addListener(ProcessorListener listener) {
        logger.debug(String.format("Add new listener to the processor '%s'", getDisplayName()));

        if (!processorListeners.contains(listener)) {
            processorListeners.add(listener);
        } else {
            logger.debug(String.format("The processor '%s' already has this listener", getDisplayName()));
        }
    }

    public void removeListener(ProcessorListener listener) {
        logger.debug(String.format("Remove listener to the processor '%s'", getDisplayName()));

        processorListeners.remove(listener);
    }

    public void start() {
        logger.info(String.format("Starting processor '%s'", getDisplayName()));

        logger.debug("Wait until page completed");

        fireProcessorMessage("Wait for page complete");

        webDriverHelper.waitPageCompleted();

        logger.debug("Start processor action");

        processorAction();
    }

    protected abstract void processorAction();

    protected void fireProcessorStateChanged(ProcessorState processorState, String message) {
        this.processorState = processorState;
        for (ProcessorListener listener : processorListeners) {
            listener.onProcessorStateChanged(processorState, message, this);
        }
    }

    protected void fireProcessorMessage(String message) {
        for (ProcessorListener listener : processorListeners) {
            listener.onProcessorMessage(message, this);
        }
    }
}
