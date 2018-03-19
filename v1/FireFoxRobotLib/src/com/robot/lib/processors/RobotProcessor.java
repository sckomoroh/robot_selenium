package com.robot.lib.processors;

public interface RobotProcessor {
    String getDisplayName();

    Object getProcessorData();

    void setProcessorData(Object data);

    ProcessorState getState();

    void start();

    void addListener(ProcessorListener listener);

    void removeListener(ProcessorListener listener);
}
