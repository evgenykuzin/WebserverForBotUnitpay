package org.jekajops.core.database;

import org.jekajops.core.utils.files.PropertiesManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionManager {
    private static final Properties properties = PropertiesManager.getProperties("database2");
    private static Connection connection = initConnection(properties);
    public static Connection getConnection() {
        return connection;
    }
    public static Connection initConnection(Properties properties) {
        System.out.println("init database connection...");
        String url = properties.getProperty("url");
        String name = properties.getProperty("name");
        String password = properties.getProperty("password");
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(url, name, password);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        if (connection == null) System.out.println("Connection is null!!!");
        return connection;
    }
    public static void resetConnection(){
        connection = initConnection(properties);
    }
}
