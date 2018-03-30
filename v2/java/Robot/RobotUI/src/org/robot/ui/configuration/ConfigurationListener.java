package org.robot.ui.configuration;

public interface ConfigurationListener {
    void onValueChanged(String propertyName, Object value);
}
