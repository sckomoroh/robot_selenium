package com.robot.ui;

import com.robot.lib.processors.ProcessorListener;
import com.robot.lib.processors.ProcessorState;
import com.robot.lib.processors.RobotProcessor;

public class UiProcessorListener implements ProcessorListener {
    private final MainDialog mainDialog;

    public UiProcessorListener(MainDialog mainDialog)
    {
        this.mainDialog = mainDialog;
    }

    @Override
    public void onProcessorStateChanged(ProcessorState processorState, String message, RobotProcessor sender) {
        mainDialog.taskTableModel.modifyMessage(sender, message);
        mainDialog.taskTableModel.modifyState(processorState, sender);
    }

    @Override
    public void onProcessorMessage(String message, RobotProcessor sender) {
        mainDialog.taskTableModel.modifyMessage(sender, message);
    }
}
