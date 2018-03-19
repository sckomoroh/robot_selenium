package com.robot.lib.processors.impl;

import com.robot.lib.processors.BaseProcessor;
import com.robot.lib.processors.ProcessorState;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesProcessor extends BaseProcessor {
    private final List<String> activityWebReferences;

    public ActivitiesProcessor(WebDriver webDriver) {
        super(webDriver);

        activityWebReferences = new ArrayList<>();
    }

    @Override
    public String getDisplayName() {
        return "Obtain activities list";
    }

    @Override
    public Object getProcessorData()
    {
        return activityWebReferences;
    }

    @Override
    protected void processorAction() {
        fireProcessorStateChanged(ProcessorState.Started, "Collect activities started");

        activityWebReferences.clear();

        List<WebElement> activityElementList = webDriverHelper.findElementsByClassName("a", "rf-project-cover__title js-project-cover-title-link");

        for (WebElement activityItem : activityElementList)
        {
            String activityWebReference = activityItem.getAttribute("href");

            activityWebReferences.add(activityWebReference);
        }

        fireProcessorStateChanged(ProcessorState.Completed, String.format("%d found activities", activityWebReferences.size()));
    }
}
