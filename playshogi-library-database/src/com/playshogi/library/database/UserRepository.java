package com.playshogi.library.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepository {

	private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

	private static final String LOGIN_SQL = "SELECT * FROM ps_user WHERE username = ? AND password_hash = ? ";
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

	public static void main(final String[] args) {
		new UserRepository(new DbConnection()).authenticateUser("Tellmarch", "test");
		new UserRepository(new DbConnection()).authenticateUser("Tellmarch", "test2");
	}
}
