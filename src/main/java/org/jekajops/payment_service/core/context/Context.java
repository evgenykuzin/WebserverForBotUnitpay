package org.jekajops.payment_service.core.context;

import java.sql.SQLException;

public class Context {
    volatile public static Settings SETTINGS;
    static {
        try {
            SETTINGS = new Settings();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
