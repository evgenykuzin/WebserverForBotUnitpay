package org.jekajops.core.context;

import org.jekajops.core.utils.files.FileManager;

import java.io.File;
import java.sql.SQLException;

public class Context {
    volatile public static String ROOT = System.getProperty("user.dir");
    volatile public static String RESOURCES = ROOT + "/src/main/resources/";
    volatile public static File KEYBOARD_CONFIG_FILE = FileManager.getFileFromResources("config/keyboardConfig.txt");
    volatile public static File SETTINGS_FILE = FileManager.getFileFromResources("properties/settings.properties");
    volatile public static File AUDIO_LIST_FILE = FileManager.getFileFromResources("config/audioList.txt");
    volatile public static Settings SETTINGS;
    static {
        try {
            SETTINGS = new Settings();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
