package com.robot.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SaveCredentialsButtonListener implements ActionListener {
    private final MainDialog mainDialog;

    public SaveCredentialsButtonListener(MainDialog mainDialog)
    {
        this.mainDialog = mainDialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainDialog.applicationPreferences.put("USERNAME", mainDialog.userNameTextField.getText());
        mainDialog.applicationPreferences.put("PASSWORD", mainDialog.passwordTextField.getText());
    }
}
