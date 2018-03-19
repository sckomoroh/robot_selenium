package com.robot.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class CommentsBrowseButtonListener implements ActionListener {
    private final MainDialog mainDialog;

    public CommentsBrowseButtonListener(MainDialog mainDialog)
    {
        this.mainDialog = mainDialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser dialog = new JFileChooser();
        if(dialog.showOpenDialog(mainDialog) == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = dialog.getSelectedFile();
            mainDialog.commentsFileTextField.setText(selectedFile.getAbsolutePath());
            mainDialog.readCommentsFromFile(selectedFile);

            mainDialog.applicationPreferences.put("COMMENTS_FILE_PATH", selectedFile.getAbsolutePath());
        }
    }
}
