package org.robot.ui.forms;

import org.robot.ui.configuration.Configuration;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class SettingsForm {
    private final Configuration config;

    private DefaultListModel commentsListModel;

    private JTextField usernameTextField;
    private JTextField passwordTextField;
    private JButton saveCredentialsButton;
    private JTextField commentFileLocationTextField;
    private JButton browseButton;
    private JList commentsList;
    private JPanel topFormPanel;


    public SettingsForm() {
        config = Configuration.getConfiguration();

        saveCredentialsButton.addActionListener(new SaveCredentialsButtonListener());
        browseButton.addActionListener(new BrowseButtonButtonListener());

        readConfigurationSettings();
    }

    private void createUIComponents() {
        commentsListModel = new DefaultListModel();
        commentsList = new JList(commentsListModel);
    }

    private void readComments(File file) {
        commentsListModel.clear();

        try {
            List<String> comments = Files.readAllLines(file.toPath());
            for (String comment : comments) {
                commentsListModel.addElement(comment);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readConfigurationSettings() {
        String userName = config.getStringValue(Configuration.UsernameName);
        if (userName != null) {
            usernameTextField.setText(userName);
        }

        String password = config.getStringValue(Configuration.PasswordName);
        if (password != null) {
            passwordTextField.setText(password);
        }

        String commentFileLocation = config.getStringValue(Configuration.CommentsFileLocationName);
        if (commentFileLocation != null) {
            commentFileLocationTextField.setText(commentFileLocation);
            readComments(new File(commentFileLocation));
        }
    }

    private class SaveCredentialsButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            config.putStringValue(Configuration.UsernameName, usernameTextField.getText());
            config.putStringValue(Configuration.PasswordName, passwordTextField.getText());
        }
    }

    private class BrowseButtonButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser dialog = new JFileChooser();
            if(dialog.showOpenDialog(MainForm.getForm()) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = dialog.getSelectedFile();

                String filePath = selectedFile.getAbsolutePath();

                commentFileLocationTextField.setText(filePath);
                config.putStringValue(Configuration.CommentsFileLocationName, filePath);

                SettingsForm.this.readComments(selectedFile);
            }
        }
    }
}
