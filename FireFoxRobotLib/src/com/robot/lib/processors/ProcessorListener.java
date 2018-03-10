package com.robot.lib.processors;

public interface ProcessorListener {
    void onProcessorStateChanged(ProcessorState processorState, String message, RobotProcessor sender);

    void onProcessorMessage(String message, RobotProcessor sender);
}
