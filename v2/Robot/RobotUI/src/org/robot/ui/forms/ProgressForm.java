package org.robot.ui.forms;

import com.webdriver.wrapper.WebDriver;
import com.webdriver.wrapper.WebDriverType;
import org.robot.ui.Task;
import org.robot.ui.TaskListener;
import org.robot.ui.TaskState;
import org.robot.ui.activities.ActivitiesTask;
import org.robot.ui.activities.ActivityItem;
import org.robot.ui.appreciate.AppreciateTask;
import org.robot.ui.comment.CommentTask;
import org.robot.ui.comment.InputCommentTaskData;
import org.robot.ui.configuration.Configuration;
import org.robot.ui.configuration.ConfigurationListener;
import org.robot.ui.follow.FollowTask;
import org.robot.ui.login.LoginTask;
import org.robot.ui.login.InputLoginTaskData;
import org.robot.ui.webdriver.CreateWebDriverTask;
import org.robot.ui.webdriver.InputCreateWebDriverTaskData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProgressForm {
    private final Configuration config;
    private final List<String> comments;
    private final String applicationLocation;
    private final boolean isWindows;
    private final Random random;

    private DefaultComboBoxModel browserTypeComboBoxModel;
    private ProgressTableModel progressTableModel;

    private JPanel formTopPanel;
    private JCheckBox followCheckBox;
    private JCheckBox appreciateCheckBox;
    private JCheckBox commentCheckBox;
    private JButton startButton;
    private JComboBox browserTypeComboBox;
    private JLabel currentPhaseLabel;
    private JProgressBar robotProgressBar;
    private JTable progressTable;

    public ProgressForm() {
        config = Configuration.getConfiguration();
        comments = new ArrayList<>();
        random = new Random();

        // /Users/anna/Documents/src/behance_robot_java/Robot/out/production/RobotUI/
        applicationLocation = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        isWindows = System.getProperty("os.name").toUpperCase().contains("WINDOWS");

        readConfiguration();

        browserTypeComboBox.addItemListener(new BrowserTypeComboBoxItemListener());
        followCheckBox.addActionListener(new FollowCheckBoxActionListener());
        appreciateCheckBox.addActionListener(new AppreciateCheckBoxActionListener());
        commentCheckBox.addActionListener(new CommentCheckBoxActionListener());
        startButton.addActionListener(new StartButtonActionListener());
        config.addListener(new ConfigurationChangedListener());
    }

    private void readConfiguration () {
        readBrowserType();
        readEnabledActions();
        readComments();
    }

    private void readBrowserType() {
        String browserType = config.getStringValue(Configuration.BrowserTypeName);
        if (browserType != null) {
            BrowserType browserValue = BrowserType.valueOf(browserType);
            browserTypeComboBoxModel.setSelectedItem(browserValue);
        }
    }

    private void readEnabledActions() {
        int enabledActions = config.getIntValue(Configuration.ActionFlagsName);
        if (enabledActions == -1) {
            enabledActions = 7; // all enabled
            config.putIntValue(Configuration.ActionFlagsName, enabledActions);
        }
        if ((enabledActions & ActionFlags.Appreciate) != 0) {
            appreciateCheckBox.setSelected(true);
        }

        if ((enabledActions & ActionFlags.Comment) != 0) {
            commentCheckBox.setSelected(true);
        }

        if ((enabledActions & ActionFlags.Follow) != 0) {
            followCheckBox.setSelected(true);
        }
    }

    private void readComments() {
        String fileLocation = config.getStringValue(Configuration.CommentsFileLocationName);
        if (fileLocation != null) {
            File file = new File(fileLocation);
            if (file.exists()) {
                readComments(file);
            }
        }
    }

    private void createUIComponents() {
        browserTypeComboBoxModel = new DefaultComboBoxModel();
        browserTypeComboBoxModel.addElement(BrowserType.Chrome);
        browserTypeComboBoxModel.addElement(BrowserType.FireFox);

        browserTypeComboBox = new JComboBox(browserTypeComboBoxModel);

        progressTableModel = new ProgressTableModel();
        progressTableModel.addColumn("Name");
        progressTableModel.addColumn("Comment");
        progressTableModel.addColumn("Follow");
        progressTableModel.addColumn("Appreciating");

        progressTable = new JTable(progressTableModel);
    }

    private void saveActionsFlags(int action) {
        int enabledActions = config.getIntValue(Configuration.ActionFlagsName);
        enabledActions ^= action;

        config.putIntValue(Configuration.ActionFlagsName, enabledActions);
    }

    private void readComments(File file) {
        comments.clear();

        try {
            List<String> comments = Files.readAllLines(file.toPath());
            for (String comment : comments) {
                this.comments.add(comment);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class BrowserTypeComboBoxItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String browserType = e.getItem().toString();
                config.putStringValue(Configuration.BrowserTypeName, browserType);
            }
        }
    }

    private class FollowCheckBoxActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ProgressForm.this.saveActionsFlags(ActionFlags.Follow);
        }
    }

    private class AppreciateCheckBoxActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ProgressForm.this.saveActionsFlags(ActionFlags.Appreciate);
        }
    }

    private class CommentCheckBoxActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ProgressForm.this.saveActionsFlags(ActionFlags.Comment);
        }
    }

    private class ConfigurationChangedListener implements ConfigurationListener {

        @Override
        public void onValueChanged(String propertyName, Object value) {
            if (propertyName.equals(Configuration.CommentsFileLocationName)) {
                File file = new File(value.toString());
                if (file.exists()) {
                    readComments(file);
                }
            }
        }
    }

    private class StartButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            setupControls();
            startTasks();
        }

        private void setupControls() {
            ProgressForm.this.startButton.setEnabled(false);
            ProgressForm.this.robotProgressBar.setIndeterminate(true);
            ProgressForm.this.currentPhaseLabel.setText("Starting...");
        }

        private void startTasks() {
            Thread tasksThread = new Thread(new TasksThread());
            tasksThread.start();
        }
    }

    private class TasksThread implements Runnable {
        private final RunTaskListener commonTaskListener;

        private WebDriver webDriver;
        private String userWebreference;
        private List<ActivityItem> activities;

        public TasksThread() {
            commonTaskListener = new RunTaskListener();
        }

        @Override
        public void run() {
            try {
                createWebDriver();
                login();
                collectActivities();
            } catch (Exception ex) {
                ProgressForm.this.startButton.setEnabled(true);
                ProgressForm.this.robotProgressBar.setIndeterminate(false);
                ProgressForm.this.robotProgressBar.setMaximum(100);
                ProgressForm.this.robotProgressBar.setValue(0);
                ProgressForm.this.currentPhaseLabel.setText("Failed");

                JOptionPane.showMessageDialog(
                        ProgressForm.this.formTopPanel,
                        ex.getMessage(),
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            ProgressForm.this.progressTableModel.removeAllItems();


            ProgressForm.this.robotProgressBar.setIndeterminate(false);
            ProgressForm.this.robotProgressBar.setMaximum(activities.size());
            ProgressForm.this.robotProgressBar.setMinimum(0);
            ProgressForm.this.robotProgressBar.setValue(0);

            for(ActivityItem activityItem : activities) {
                createActivitiesTask(activityItem);
            }

            for (int i=0; i<ProgressForm.this.progressTableModel.getRowCount(); i++) {
                String taskPhase = String.format("Process activity '%s'", ProgressForm.this.progressTableModel.getName(i));
                ProgressForm.this.currentPhaseLabel.setText(taskPhase);

                String activityUrl = ProgressForm.this.progressTableModel.getWebUrl(i);
                webDriver.navigate(activityUrl, true);

                Task[] tasks = ProgressForm.this.progressTableModel.getTasks(i);
                for (Task task : tasks) {
                    if (task == null) {
                        continue;
                    }

                    task.run();
                }

                ProgressForm.this.robotProgressBar.setValue(i + 1);
            }
        }

        private void createWebDriver() throws Exception {
            InputCreateWebDriverTaskData inputCreateWebDriverTaskData = new InputCreateWebDriverTaskData();
            inputCreateWebDriverTaskData.startUrl = "http://www.behance.net";

            inputCreateWebDriverTaskData.driverLocation = getDriverLocation();

            if ((BrowserType) browserTypeComboBoxModel.getSelectedItem() == BrowserType.FireFox) {
                inputCreateWebDriverTaskData.driverType = WebDriverType.Firefox;
            }
            else {
                inputCreateWebDriverTaskData.driverType = WebDriverType.Chrome;
            }

            CreateWebDriverTask createWebDriverTask = new CreateWebDriverTask();
            createWebDriverTask.addListener(commonTaskListener);
            createWebDriverTask.setTaskData(inputCreateWebDriverTaskData);

            createWebDriverTask.run();

            createWebDriverTask.removeListener(commonTaskListener);

            webDriver = createWebDriverTask.getTaskData();

            if (createWebDriverTask.getTaskState() == TaskState.Failed) {
                throw createWebDriverTask.getTaskException();
            }
        }

        private String getDriverLocation() {
            String driverFileName = null;
            BrowserType browserType = (BrowserType) browserTypeComboBoxModel.getSelectedItem();
            switch (browserType) {
                case Chrome:
                    driverFileName = "chromedriver";
                    break;
                case FireFox:
                    driverFileName = "geckodriver";
                    break;
            }

            String fileTemplate = ProgressForm.this.isWindows
                    ? "%s/%s.exe"
                    : "%s/%s";

            return String.format(fileTemplate, ProgressForm.this.applicationLocation, driverFileName);
        }

        private void login() {
            String userName = config.getStringValue(Configuration.UsernameName);
            String password = config.getStringValue(Configuration.PasswordName);

            InputLoginTaskData inputLoginTaskData = new InputLoginTaskData();
            inputLoginTaskData.username = userName;
            inputLoginTaskData.password = password;

            LoginTask loginTask = new LoginTask(webDriver);
            loginTask.setTaskData(inputLoginTaskData);

            loginTask.addListener(commonTaskListener);

            loginTask.run();

            loginTask.removeListener(commonTaskListener);

            userWebreference = loginTask.getTaskData();
        }

        private void collectActivities() {
            ActivitiesTask activitiesTask = new ActivitiesTask(webDriver);

            activitiesTask.addListener(commonTaskListener);

            activitiesTask.run();

            activitiesTask.removeListener(commonTaskListener);

            activities = activitiesTask.getTaskData();

            ProgressForm.this.currentPhaseLabel.setText(String.format("Found %d activities", activities.size()));
        }

        private void createActivitiesTask(ActivityItem activityItem) {
            Task[] tasks = new Task[3];
            if (ProgressForm.this.commentCheckBox.isSelected()) {
                InputCommentTaskData taskData = new InputCommentTaskData();
                taskData.userWebReference = userWebreference;
                taskData.comment = ProgressForm.this.comments.get(ProgressForm.this.random.nextInt(ProgressForm.this.comments.size()));

                tasks[1] = new CommentTask(webDriver, activityItem.getName());
                tasks[1].setTaskData(taskData);
            }

            if (ProgressForm.this.followCheckBox.isSelected()) {
                tasks[0] = new FollowTask(webDriver, activityItem.getName());
            }

            if (ProgressForm.this.appreciateCheckBox.isSelected()) {
                tasks[2] = new AppreciateTask(webDriver, activityItem.getName());
            }

            ProgressForm.this.progressTableModel.addTasks(activityItem.getWebReference(), activityItem.getName(), tasks);
        }
    }

    private class RunTaskListener implements TaskListener {

        @Override
        public void onTaskStateChanged(Task sender, TaskState state) {
        }

        @Override
        public void onTaskPhaseChanged(Task sender, String phase) {
            currentPhaseLabel.setText(phase);
        }
    }
}
