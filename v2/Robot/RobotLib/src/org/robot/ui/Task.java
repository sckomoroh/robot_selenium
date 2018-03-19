package org.robot.ui;

public interface Task<TInput, TOutput> {
    TOutput getTaskData();

    void setTaskData(TInput inputData);

    String getDisplayName();

    TaskState getTaskState();

    String getTaskPhase();

    void run();

    void addListener(TaskListener listener);

    void removeListener(TaskListener listener);

    Exception getTaskException();
}
