package org.jekajops.core.database.wrappers;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import org.jekajops.core.database.DatabaseConnectionManager;

import java.sql.SQLException;

public interface DatabaseWrapper<T> {
    T method() throws SQLException;

    default T execute() {
        try {
            return method();
        }
        catch (CommunicationsException ce) {
            ce.printStackTrace();
            DatabaseConnectionManager.resetConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
