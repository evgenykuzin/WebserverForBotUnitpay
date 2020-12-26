package org.jekajops.core.utils.files;

import org.jekajops.App;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileManager {
    public static File download(String urlString, String fileName, String suffix) throws IOException {
        URL url = new URL(urlString);
        Path path = Files.createTempFile(Paths.get("."), fileName, suffix);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
        fileOutputStream.getChannel()
                .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        System.out.println("return file");
        return path.toFile();
    }

    public static List<String> readFile(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static void writeToFile(File file, String text) {
        try {
            Files.writeString(file.toPath(), text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getFileFromResources(String path) {
        String filePath = "/src/main/resources/" + path;
        String projectPath = System.getProperty("user.dir");
        if (projectPath == null) {
            projectPath = new File("").getAbsolutePath();
        }
        if (projectPath.isEmpty()) {
            projectPath = App.class.getResource("").toString();
        }
        File projectDir = new File(projectPath);
        File file = new File(projectDir.getAbsolutePath() + filePath);
        for (int i = 0; i < 10; i++) {
            if (file.isFile() && file.exists()) {
                break;
            }
            if (projectDir != null &&
                    projectDir.exists()) {
                if (projectDir.getParentFile() != null) {
                    projectDir = projectDir.getParentFile();
                }
                file = new File(projectDir, filePath);
            }
        }
        return file;
    }


}
