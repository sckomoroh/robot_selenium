package org.robot.ui;

public interface TaskListener {
    void onTaskStateChanged(Task sender, TaskState state);

    void onTaskPhaseChanged(Task sender, String phase);
}
