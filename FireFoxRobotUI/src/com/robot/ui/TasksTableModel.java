package com.robot.ui;

import com.robot.lib.processors.ProcessorState;
import com.robot.lib.processors.RobotProcessor;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class TasksTableModel extends DefaultTableModel {
    private final List<RobotProcessor> processors;

    TasksTableModel() {
        processors = new ArrayList<>();

        addColumn("name");
        addColumn("State");
        addColumn("Message");
    }

    public void addProcessorItem(RobotProcessor robotProcessor) {
        Object[] rowData = new Object[3];
        rowData[0] = robotProcessor.getDisplayName();
        rowData[1] = robotProcessor.getState();
        rowData[2] = "Processor is pending";

        this.addRow(rowData);

        processors.add(robotProcessor);
    }

    public ProcessorState getStatus(int row) {
        return (ProcessorState)this.getValueAt(row, 1);
    }

    public void modifyState(ProcessorState processorState, RobotProcessor robotProcessor) {
        int rowIndex = processors.indexOf(robotProcessor);

        this.setValueAt(robotProcessor.getState(), rowIndex, 1);
    }

    public void modifyMessage(RobotProcessor robotProcessor, String message) {
        int rowIndex = processors.indexOf(robotProcessor);

        this.setValueAt(message, rowIndex, 2);
    }
}
