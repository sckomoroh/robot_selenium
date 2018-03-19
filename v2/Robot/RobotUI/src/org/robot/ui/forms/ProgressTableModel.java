package org.robot.ui.forms;

import org.robot.ui.Task;
import org.robot.ui.TaskListener;
import org.robot.ui.TaskState;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ProgressTableModel extends DefaultTableModel {
    private final List<RowItem> rows;
    private final RunningTaskListener runningTaskListener;

    public ProgressTableModel() {
        addColumn("Name");
        addColumn("Comment");
        addColumn("Follow");
        addColumn("Appreciating");

        rows = new ArrayList<>();
        runningTaskListener = new RunningTaskListener();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public int getRowCount() {
        if (rows == null) {
            return 0;
        }

        return rows.size();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Name";
            case 1:
                return "Follow";
            case 2:
                return "Comment";
            case 3:
                return "Appreciating";
        }

        return null;
    }

    @Override
    public Object getValueAt(int row, int column) {
        RowItem rowItem = rows.get(row);
        if (column == 0) {
            return rowItem.name;
        }

        if (column > 0 && column < 4) {
            Task task = rowItem.tasks[column - 1];
            if (task == null) {
                return "Ignored";
            }

            return task.getTaskState();
        }

        return null;
    }

    public void removeAllItems() {
        int rowsCount = rows.size();

        rows.clear();

        fireTableRowsDeleted(0, rowsCount);
    }

    public Task[] getTasks(int row) {
        return rows.get(row).tasks;
    }

    public String getWebUrl(int row) {
        return rows.get(row).webUrl;
    }

    public String getName (int row) {
        return rows.get(row).name;
    }

    public void addTasks(String webUrl, String name, Task[] tasks) {
        for (Task taskItem : tasks) {
            if (taskItem != null) {
                taskItem.addListener(runningTaskListener);
            }
        }

        RowItem rowItem = new RowItem(webUrl, name, tasks);

        rows.add(rowItem);
        int index = rows.indexOf(rowItem);

        fireTableRowsInserted(index, index);
    }

    private class RowItem {
        public String webUrl;
        public String name;
        public Task[] tasks;

        public RowItem(String webUrl, String name, Task[] tasks) {
            this.webUrl = webUrl;
            this.name = name;
            this.tasks = tasks;
        }
    }

    private class RunningTaskListener implements TaskListener {

        @Override
        public void onTaskStateChanged(Task sender, TaskState state) {
            ProgressTableModel.this.fireTableDataChanged();
        }

        @Override
        public void onTaskPhaseChanged(Task sender, String phase) {

        }
    }
}
