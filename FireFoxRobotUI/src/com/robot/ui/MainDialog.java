package com.robot.ui;

import com.robot.lib.processors.RobotProcessor;
import com.robot.lib.processors.impl.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.prefs.Preferences;

class MainDialog extends JDialog {
    private static final Logger logger = LogManager.getLogger();

    private final UiProcessorListener processorListener;
    private final Random random = new Random();

    private JPanel contentPane;
    private JCheckBox commentCheckBox;
    private JCheckBox followCheckBox;
    private JCheckBox appreciateCheckBox;
    private JButton startRobotButton;
    private JTable robotResultTable;
    private JButton driverBrowseButton;
    private JButton commentBrowseButton;
    private JList commentsList;
    private JButton saveCredentialsButton;
    private DefaultListModel commentsListModel;
    private List<String> comments = new ArrayList<>();
    private WebDriver webDriver;


    public TasksTableModel taskTableModel;
    public JTextField userNameTextField;
    public JTextField passwordTextField;
    public JTextField geckoDriverLocationTextField;
    public JTextField commentsFileTextField;

    public final Preferences applicationPreferences;

    private MainDialog() {
        logger.info("Start application");

        setContentPane(contentPane);
        setModal(true);

        logger.debug("Read application settings");
        applicationPreferences = Preferences.userRoot();

        String geckoDriverPath = applicationPreferences.get("GECKO_DRIVER_PATH", null);
        String commentsFilePath = applicationPreferences.get("COMMENTS_FILE_PATH", null);
        String userName = applicationPreferences.get("USERNAME", null);
        String password = applicationPreferences.get("PASSWORD", null);

        logger.debug(String.format(
                "Driver location: '%s', comments file: '%s', username: '%s', password: '%s'",
                geckoDriverPath,
                commentsFilePath,
                userName,
                password));

        logger.debug("Set system property for the driver location");

        System.setProperty("webdriver.gecko.driver", geckoDriverPath == null ? "" : geckoDriverPath);

        logger.debug("Setup UI elements with configuration values");

        geckoDriverLocationTextField.setText(geckoDriverPath);

        if (commentsFilePath != null) {
            logger.info(String.format("Read comments from file: '%s'", commentsFilePath));
            File commentsFile = new File(commentsFilePath);
            if (commentsFile.exists()) {
                commentsFileTextField.setText(commentsFilePath);
                readCommentsFromFile(commentsFile);
            } else {
                logger.warn(String.format("Comments file '%s' does not exists", commentsFilePath));
            }
        }

        userNameTextField.setText(userName);
        passwordTextField.setText(password);

        driverBrowseButton.addActionListener(new DriverBrowserButtonListener(this));
        commentBrowseButton.addActionListener(new CommentsBrowseButtonListener(this));
        saveCredentialsButton.addActionListener(new SaveCredentialsButtonListener(this));
        startRobotButton.addActionListener(new StartRobotButtonListener(this));

        processorListener = new UiProcessorListener(this);
    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        commentsListModel = new DefaultListModel<String>();
        commentsList = new JList<String>(commentsListModel);
        commentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        taskTableModel = new TasksTableModel();

        robotResultTable = new JTable(taskTableModel);
        robotResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        robotResultTable.getColumnModel().getColumn(1).setCellRenderer(new StatusTableCellRenderer());
    }

    public void readCommentsFromFile(File file) {
        commentsListModel.clear();

        try {
            comments = Files.readAllLines(file.toPath());

            logger.debug(String.format("Found %d lines in file with comments", comments.size()));

            for(String commentLine : comments) {
                commentsListModel.addElement(commentLine);
            }
        }
        catch (IOException ex) {
            logger.error(String.format("Unable to open file '%s'", file.getAbsolutePath()), ex);
        }
    }

    public void startRobot() {
        logger.info("Start ROBOT");

        startRobotButton.setEnabled(false);

        Thread robotThread = new Thread(new Runnable() {
            @Override
            public void run() {
                createWebDriver();

                logCheckBoxStates();

                RobotProcessor loginProcessor = createLoginProcessor();
                RobotProcessor activitiesProcessor = createActivitiesProcessor();

                String userWebReference = executeLoginProcessor(loginProcessor);
                List<String> activityWebReferences = executeActivitiesProcessor(activitiesProcessor);

                Map<String, List<RobotProcessor>> contentProcessorsMap = buildContentProcessorsMap(activityWebReferences, userWebReference);

                executeContentProcessors(contentProcessorsMap);
            }
        });

        robotThread.start();
    }

    private void createWebDriver() {
        if (webDriver == null) {
            logger.info("Creating FireFox WEB driver");

            webDriver = new FirefoxDriver();

            logger.info("Navigate to the WEB page");

            webDriver.get("https://www.behance.net/");
        }
    }

    private RobotProcessor createLoginProcessor() {
        logger.debug("Create login processor");

        LoginProcessorData loginProcessorData = new LoginProcessorData();
        loginProcessorData.Username = userNameTextField.getText();
        loginProcessorData.Password = passwordTextField.getText();

        RobotProcessor loginProcessor = new LoginProcessor(webDriver);
        loginProcessor.setProcessorData(loginProcessorData);
        loginProcessor.addListener(processorListener);

        taskTableModel.addProcessorItem(loginProcessor);

        return loginProcessor;
    }

    private RobotProcessor createActivitiesProcessor() {
        logger.debug("Create activities processor");

        RobotProcessor activitiesProcessor = new ActivitiesProcessor(webDriver);
        activitiesProcessor.addListener(processorListener);

        taskTableModel.addProcessorItem(activitiesProcessor);

        return activitiesProcessor;
    }

    private String executeLoginProcessor(RobotProcessor loginProcessor) {
        logger.info("Start login processor");

        loginProcessor.start();

        String userWebReference = (String)loginProcessor.getProcessorData();

        logger.info(String.format("User WEB reference: '%s'", userWebReference));

        return userWebReference;
    }

    private List<String> executeActivitiesProcessor(RobotProcessor activitiesProcessor) {
        logger.info("Start activities processor");

        activitiesProcessor.start();

        List<String> activityReferences = (List<String>) activitiesProcessor.getProcessorData();

        logger.info(String.format("Found %d activities", activityReferences.size()));

        return activityReferences;
    }

    private void logCheckBoxStates() {
        if (!commentCheckBox.isSelected()) {
            logger.info("Comments unchecked. Will skipped");
        }

        if (!followCheckBox.isSelected()) {
            logger.info("Following unchecked. Will skipped");
        }

        if (!appreciateCheckBox.isSelected()) {
            logger.info("Appreciating unchecked. Will skipped");
        }
    }

    private Map<String, List<RobotProcessor>> buildContentProcessorsMap(List<String> activityReferences, String userWebReference) {
        logger.debug("Build processors map");

        Map<String, List<RobotProcessor>> actionsProcessorsMap = new HashMap<>();

        for(String activityUrl : activityReferences) {
            List<RobotProcessor> itemProcessors = new ArrayList<>();

            if (followCheckBox.isSelected()) {
                RobotProcessor processor = createFollowProcessor(activityUrl);
                itemProcessors.add(processor);
            }

            if (appreciateCheckBox.isSelected()) {
                RobotProcessor processor = createAppreciateProcessor(activityUrl);
                itemProcessors.add(processor);
            }

            if (commentCheckBox.isSelected()) {
                RobotProcessor processor = createCommentProcessor(activityUrl, userWebReference);
                itemProcessors.add(processor);
            }

            actionsProcessorsMap.put(activityUrl, itemProcessors);
        }

        return actionsProcessorsMap;
    }

    private RobotProcessor createFollowProcessor(String activityUrl) {
        RobotProcessor followProcessor = new FollowProcessor(webDriver);
        followProcessor.setProcessorData(activityUrl);
        followProcessor.addListener(processorListener);
        taskTableModel.addProcessorItem(followProcessor);

        return followProcessor;
    }

    private RobotProcessor createAppreciateProcessor(String activityUrl){
        RobotProcessor appreciateProcessor = new AppreciateProcessor(webDriver);
        appreciateProcessor.setProcessorData(activityUrl);
        appreciateProcessor.addListener(processorListener);
        taskTableModel.addProcessorItem(appreciateProcessor);

        return appreciateProcessor;
    }

    private RobotProcessor createCommentProcessor(String activityUrl, String userWebReference) {
        CommentProcessorData processorData = new CommentProcessorData();
        processorData.activityWebUrl = activityUrl;
        processorData.userWebReference = userWebReference;
        processorData.comment = obtainComment(); // TODO:

        RobotProcessor commentProcessor = new CommentProcessor(webDriver);
        commentProcessor.setProcessorData(processorData);
        commentProcessor.addListener(processorListener);
        taskTableModel.addProcessorItem(commentProcessor);

        return commentProcessor;
    }

    private String obtainComment() {
        int commentsCount = comments.size();
        if (commentsCount < 1) {
            logger.error("No any comment in the list");
            return null;
        }

        int index = random.nextInt(commentsCount);
        return comments.get(index);
    }

    private void executeContentProcessors(Map<String, List<RobotProcessor>> contentProcessorsMap) {
        logger.debug("Start content processors");

        for(String activityUrl : contentProcessorsMap.keySet())
        {
            List<RobotProcessor> itemProcessors = contentProcessorsMap.get(activityUrl);

            webDriver.get(activityUrl);

            for(RobotProcessor robotProcessorItem : itemProcessors)
            {
                robotProcessorItem.start();
            }
        }

        logger.info("All processors completed");
    }
}
