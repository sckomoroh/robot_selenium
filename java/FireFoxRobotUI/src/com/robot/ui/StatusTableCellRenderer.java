package com.robot.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

class StatusTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        //Cells are by default rendered as a JLabel.
        JLabel cellLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        //Get the status for the current row.
        TasksTableModel tableModel = (TasksTableModel) table.getModel();
        switch (tableModel.getStatus(row)) {
            case Pending:
                cellLabel.setForeground(new Color(64, 64, 64));
                break;
            case Completed:
                cellLabel.setForeground(new Color(0, 127, 14));
                break;
            case Started:
                cellLabel.setForeground(new Color(27, 27, 124));
                break;
            case Failed:
                cellLabel.setForeground(new Color(127, 0, 0));
                break;
            case Skipped:
                cellLabel.setForeground(new Color(124, 49, 12));
                break;
        }

        //Return the JLabel which renders the cell.
        return cellLabel;
    }
}
