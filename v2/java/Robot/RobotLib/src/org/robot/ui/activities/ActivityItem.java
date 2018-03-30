package org.robot.ui.activities;

public class ActivityItem {
    private final String webReference;
    private final String name;

    public ActivityItem(String webReference, String name) {
        this.webReference = webReference;
        this.name = name;
    }

    public String getWebReference() {
        return webReference;
    }

    public String getName() {
        return name;
    }
}
