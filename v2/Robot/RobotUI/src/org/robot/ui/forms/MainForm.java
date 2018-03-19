package org.robot.ui.forms;

import javax.swing.*;

public class MainForm extends JFrame {
    private static MainForm mainForm;

    private JPanel contentPane;
    private JTabbedPane tabbedPane1;

    public MainForm() {
        setContentPane(contentPane);

        mainForm = this;

        setTitle("Robot");
        setType(Type.NORMAL);
        setVisible(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    public static MainForm getForm() {
        return mainForm;
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        new MainForm();
    }
}
