package com.playshogi.library.database;

public class Users {

	private final DbConnection dbConnection;

	public Users(final DbConnection dbConnection) {
		this.dbConnection = dbConnection;
	}

	public boolean authenticateUser(final String username, final String password) {
		return true;
	}
}
