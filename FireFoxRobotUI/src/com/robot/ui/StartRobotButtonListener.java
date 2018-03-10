package com.robot.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StartRobotButtonListener  implements ActionListener {
    private final MainDialog mainDialog;

    public StartRobotButtonListener(MainDialog mainDialog)
    {
        this.mainDialog = mainDialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainDialog.startRobot();
    }
}
