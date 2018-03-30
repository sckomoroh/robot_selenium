package com.robot.ui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class DriverBrowserButtonListener implements ActionListener {
    private final MainDialog mainDialog;

    public DriverBrowserButtonListener(MainDialog mainDialog)
    {
        this.mainDialog = mainDialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser dialog = new JFileChooser();

        dialog.setDialogTitle("Select GECKO driver");

        dialog.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String fileName = f.getName().toLowerCase();
                return fileName.startsWith("geckodriver") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "GECKO driver files";
            }
        });

        if(dialog.showOpenDialog(mainDialog) == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = dialog.getSelectedFile();

            String pathToDriver = selectedFile.getAbsolutePath();

            mainDialog.geckoDriverLocationTextField.setText(pathToDriver);
            mainDialog.applicationPreferences.put("GECKO_DRIVER_PATH", pathToDriver);
            System.setProperty("webdriver.gecko.driver", pathToDriver);
        }
    }
}
