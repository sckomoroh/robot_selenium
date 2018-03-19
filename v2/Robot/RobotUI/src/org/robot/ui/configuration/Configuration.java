package org.robot.ui.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Configuration {
    private static Configuration instance = null;

    private final Preferences configuration;
    private final List<ConfigurationListener> listeners;

    public final static String UsernameName = "UserLogin";
    public final static String PasswordName = "UserPassword";
    public final static String CommentsFileLocationName = "CommentsFileLocation";
    public final static String BrowserTypeName = "BrowserType";
    public final static String ActionFlagsName = "ActionFlags";

    private Configuration() {
        configuration = Preferences.userNodeForPackage(Configuration.class);
        listeners = new ArrayList<>();
    }

    public static Configuration getConfiguration() {
        if (instance == null) {
            instance = new Configuration();
        }

        return instance;
    }

    public void addListener(ConfigurationListener listener) {
        if (listeners.contains(listener)) {
            return;
        }

        listeners.add(listener);
    }

    public void removeListener(ConfigurationListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public String getStringValue(String name) {
        return configuration.get(name, null);
    }

    public int getIntValue(String name) {
        return configuration.getInt(name, -1);
    }

    public void putStringValue(String name, String value) {
        configuration.put(name, value);

        fireValueChanged(name, value);
    }

    public void putIntValue(String name, int value) {
        configuration.putInt(name, value);

        fireValueChanged(name, value);
    }

    private void fireValueChanged(String name, Object newValue) {
        for (ConfigurationListener listener : listeners) {
            listener.onValueChanged(name, newValue);
        }
    }
}
