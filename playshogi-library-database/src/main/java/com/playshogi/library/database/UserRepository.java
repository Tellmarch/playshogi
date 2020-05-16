package com.playshogi.library.database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepository {

    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    private static final String LOGIN_SQL = "SELECT * FROM ps_user WHERE username = ? AND password_hash = ? ";
    private static final String INSERT_USER = "INSERT INTO `playshogi`.`ps_user` "
            + "(`username`, `password_hash`)" + " VALUES ( ?, ?);";

    private final DbConnection dbConnection;

    public UserRepository(final DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public AuthenticationResult authenticateUser(final String username, final String password) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(LOGIN_SQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                LOGGER.log(Level.INFO, "Found user: " + username + " with id: " + rs.getInt("id"));
                return AuthenticationResult.LOGIN_OK;
            } else {
                LOGGER.log(Level.INFO, "Did not find user: " + username);
                return AuthenticationResult.UNKNOWN;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the user in db", e);
            return AuthenticationResult.UNAVAILABLE;
        }
    }

    public int insertUser(final String userName, final String password_hash) {

        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password_hash);
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                key = rs.getInt(1);
                LOGGER.log(Level.INFO, "Inserted user with index " + key);
            } else {
                LOGGER.log(Level.SEVERE, "Could not insert user");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the user in db", e);
        }

        return key;
    }


    public static void main(final String[] args) {
        new UserRepository(new DbConnection()).insertUser("Tellmarch", "test");
        new UserRepository(new DbConnection()).authenticateUser("Tellmarch", "test");
        new UserRepository(new DbConnection()).authenticateUser("Tellmarch", "test2");
    }
}
