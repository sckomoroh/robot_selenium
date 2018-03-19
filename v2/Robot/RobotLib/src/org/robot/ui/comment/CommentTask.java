package org.robot.ui.comment;

import com.webdriver.wrapper.WebDriver;
import com.webdriver.wrapper.WebElement;
import org.robot.ui.WebDriverTaskBase;

import java.util.List;

public class CommentTask extends WebDriverTaskBase<InputCommentTaskData, Object> {
    public CommentTask(WebDriver webDriver, String name) {
        super(webDriver);
        displayName = String.format("Commenting '%s'", name);
    }

    @Override
    protected boolean runInternal() throws Exception {
        if (isExists()) {
            setPhase("Already commented");
            return false;
        }

        doComment();
        waitComment();

        return true;
    }

    private void waitComment() {
        setPhase("Wait while comment is appearing");

        while (!isExists()) {}
    }

    private boolean isExists() {
        setPhase("Check for the comment");

        List<WebElement> commentItems = webDriver.getElementsByClassName("rf-avatar js-avatar js-mini-profile");
        for(WebElement commentItem : commentItems) {
            String userWebReference = commentItem.getAttribute("href");
            if (inputData.userWebReference.equals(userWebReference)) {
                return true;
            }
        }

        return false;
    }

    private void doComment() {
        setPhase("Commenting");

        WebElement commentInputElement = webDriver.getElementById("comment");
        WebElement commentSubmitButton = webDriver.getElementsByClassName("form-button js-rf-button rf-button js-submit").iterator().next();

        commentInputElement.setElementValue(inputData.comment);

        commentSubmitButton.click();

        while (!isExists()) {
        }
    }
}
