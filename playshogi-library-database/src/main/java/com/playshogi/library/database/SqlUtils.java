package com.playshogi.library.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class SqlUtils {

    public static Integer getInteger(final ResultSet resultSet, final String fieldName) throws SQLException {
        Integer result = resultSet.getInt(fieldName);
        if (resultSet.wasNull()) {
            result = null;
        }
        return result;
    }

    public static void setInteger(final PreparedStatement preparedStatement, final int fieldIndex,
                                  final Integer value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(fieldIndex, Types.INTEGER);
        } else {
            preparedStatement.setInt(fieldIndex, value);
        }
    }

}
