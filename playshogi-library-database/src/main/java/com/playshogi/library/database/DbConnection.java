package com.playshogi.library.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbConnection {

    private static final Logger LOGGER = Logger.getLogger(DbConnection.class.getName());

    private static final int MAX_RETRIES = 3;
    private final String database = "playshogi";
    private final String host = "localhost";
    private final int port = 3306;
    private final String parameters = "?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC" +
            "&autoReconnect=true";
    private final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + parameters;
    private final String user = "playshogi";
    private final String password = "playshogiDB1";

    private Connection connection;

    public DbConnection() {
        LOGGER.log(Level.INFO, "Initiating DbConnection");
    }

    public void start() {
        LOGGER.log(Level.INFO, "Connecting to database");

        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                connection = DriverManager.getConnection(url, user, password);
                return;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Try " + i + ": Could not establish JDBC connection", e);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public Connection getConnection() {
        if (!isValid()) {
            start();
        }
        return connection;
    }

    private boolean isValid() {
        if (connection == null) return false;

        try {
            if (!connection.isValid(1000)) return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Connection error: ", e);
            return false;
        }

        return true;
    }
}
