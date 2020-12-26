package org.jekajops.core.utils.files;

import com.amazonaws.util.StringInputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.jekajops.call_api.exceptions.LoaderException;

import java.io.*;
import java.net.Inet4Address;
import java.net.URL;
import java.util.Properties;

public class FTPManger {
    private static final Properties properties = PropertiesManager.getProperties("ftp");
    private static final String host = properties.getProperty("host");
    private static final String username = properties.getProperty("username");
    private static final String password = properties.getProperty("password");
    private static final int port = Integer.parseInt(properties.getProperty("port"));
    private static final String authorizedLink = "ftp://" +
            username + ":" + password + "@" + host;


    public static FTPClient initFTPClient() {
        FTPClient ftpClient = new FTPClient();
        try {
            System.setProperty("sun.net.spi.nameservice.provider.1", "dns.sun");
            Inet4Address address = (Inet4Address) Inet4Address.getByName(host);
            ftpClient.connect(address, port);
            ftpClient.login(username, password);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return ftpClient;
    }

    public static File downloadFromFTP(String fileName) throws IOException {
        File file = new File(FileManager.getFileFromResources("/audio/temp/").getAbsolutePath() + fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        var ftpClient = initFTPClient();
        boolean isDownloaded = ftpClient.retrieveFile(fileName, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        ftpClient.logout();
        ftpClient.disconnect();

        return file;
    }

    public static String uploadToFTP(File file) throws LoaderException, IOException {
        var ftpClient = initFTPClient();
        FileInputStream fis = new FileInputStream(file);
        ftpClient.enterLocalPassiveMode();
        boolean uploaded = ftpClient.storeFile(file.getName(), fis);
        String errorMsg = ftpClient.getReplyString();
        ftpClient.logout();
        ftpClient.disconnect();
        System.out.println(file.getName());
        if (!uploaded) {
            throw new LoaderException("File FTP upload error: " + errorMsg);
        }
        return getFileLink(file.getName());
    }

    public static String uploadToFTP(String link, String fileName) throws LoaderException, IOException {
        var ftpClient = initFTPClient();
        URL url = new URL(link);
        InputStream is = url.openStream();
        ftpClient.enterLocalPassiveMode();
        boolean uploaded = ftpClient.storeFile(fileName, is);
        String errorMsg = ftpClient.getReplyString();
        ftpClient.logout();
        ftpClient.disconnect();
        if (!uploaded) {
            throw new LoaderException("File FTP upload error: " + errorMsg);
        }
        return getFileLink(fileName);
    }

    public static String getFileLink(String path) {
        return authorizedLink + "/" + path;
    }

    public static String getFileLinkFromTmp(String fileName) {
        return getFileLink("tmp/" + fileName);
    }

}
