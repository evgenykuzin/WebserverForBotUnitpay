package org.jekajops.payment_service.core.database.wrappers;

import java.sql.SQLException;

public interface DatabaseVoidWrapper extends DatabaseWrapper<Void> {

    @Override
    default Void method() throws SQLException {
        methodVoid();
        return null;
    }
    void methodVoid() throws SQLException;
}
