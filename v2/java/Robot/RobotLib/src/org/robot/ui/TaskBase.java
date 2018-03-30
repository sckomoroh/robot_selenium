package org.robot.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class TaskBase<TInput, TOutput> implements Task<TInput, TOutput> {
    private final static Logger logger = LogManager.getLogger();

    private final List<TaskListener> listeners;

    private Exception taskException;

    private TaskState taskState;
    private String taskPhase;

    protected TInput inputData;
    protected TOutput outputData;
    protected String displayName;

    protected TaskBase() {
        listeners = new ArrayList<>();

        setState(TaskState.Pending);
    }

    @Override
    public TOutput getTaskData() {
        return outputData;
    }

    @Override
    public void setTaskData(TInput inputData) {
        this.inputData = inputData;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public TaskState getTaskState() {
        return taskState;
    }

    @Override
    public String getTaskPhase() {
        return taskPhase;
    }

    @Override
    public void addListener(TaskListener listener) {
        if (listeners.contains(listener)) {
            return;
        }

        listeners.add(listener);
    }

    @Override
    public void removeListener(TaskListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    @Override
    public Exception getTaskException() {
        return taskException;
    }

    @Override
    public void run() {
        logger.info(String.format("Start TASK: '%s'", getDisplayName()));

        try {
            setState(TaskState.Preparing);

            if (runInternal()) {
                setState(TaskState.Completed);
                return;
            }

            setState(TaskState.Skipped);
        } catch (Exception ex) {
            logger.error(String.format("TASK failed: '%s'", getDisplayName()), ex);

            setState(TaskState.Failed);
            taskException = ex;
        }
    }

    protected abstract boolean runInternal() throws Exception;

    protected void setPhase(String phase) {
        logger.trace(String.format("TASK: '%s' changed PAHSE to '%s'", getDisplayName() ,phase));

        this.taskPhase = phase;
        firePhaseChanged();
    }

    protected void setState(TaskState state) {
        logger.trace(String.format("TASK: '%s' changed STATE to '%s'", getDisplayName() ,state));

        this.taskState = state;
        fireStateChanged();
    }

    private void firePhaseChanged() {
        for (TaskListener listener : listeners) {
            listener.onTaskPhaseChanged(this, taskPhase);
        }
    }

    private void fireStateChanged() {
        for (TaskListener listener : listeners) {
            listener.onTaskStateChanged(this, taskState);
        }
    }
}
