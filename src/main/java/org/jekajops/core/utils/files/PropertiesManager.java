package org.jekajops.core.utils.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {
    public static Properties getProperties(String name) {
        return getProperties(FileManager.getFileFromResources("properties/"+name+".properties"));
    }

    public static Properties getProperties(File file) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }
}
