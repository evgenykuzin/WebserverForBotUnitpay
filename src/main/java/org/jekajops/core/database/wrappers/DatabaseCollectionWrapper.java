package org.jekajops.core.database.wrappers;

import java.sql.SQLException;
import java.util.Collection;

public interface DatabaseCollectionWrapper<T> extends DatabaseWrapper<Collection<T>> {
    Collection<T> method() throws SQLException;
}
