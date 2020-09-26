package com.playshogi.library.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlUtils {

    public static Integer getInteger(final ResultSet resultSet, final String fieldName) throws SQLException {
        Integer result = resultSet.getInt(fieldName);
        if (resultSet.wasNull()) {
            result = null;
        }
        return result;
    }

}
