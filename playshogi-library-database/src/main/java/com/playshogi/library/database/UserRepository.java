package com.playshogi.library.database;

import com.playshogi.library.database.models.PersistentUserProblemStats;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepository {

    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    private static final String FIND_SQL = "SELECT * FROM ps_user WHERE username = ?";
    private static final String LOGIN_SQL = "SELECT * FROM ps_user WHERE username = ? AND password_hash = ? ";
    private static final String INSERT_USER = "INSERT INTO `playshogi`.`ps_user` "
            + "(`username`, `password_hash`)" + " VALUES ( ?, ?);";

    private static final String INSERT_USER_PB_STATS = "INSERT INTO `playshogi`.`ps_userpbstats` "
            + "(`user_id`, `problem_id`, `time_spent_ms`, `correct`)" + " VALUES ( ?, ?, ?, ?);";
    private static final String GET_USER_PB_STATS = "SELECT * from playshogi.ps_userpbstats WHERE  user_id = ? ORDER " +
            "BY timestamp_attempted DESC;";

    private static final String INSERT_USER_HIGHSCORE = "INSERT INTO `playshogi`.`ps_highscore` "
            + "(`name`, `score`, `user_id`, `event`)" + " VALUES ( ?, ?, ?, ?);";

    private static final String GET_USER_HIGHSCORES = "SELECT name, max(score) as score FROM ps_highscore WHERE event" +
            " = ? GROUP BY name ORDER BY score DESC LIMIT 20;";

    private final DbConnection dbConnection;

    public UserRepository(final DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public AuthenticationResult authenticateUser(final String username, final String password) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(LOGIN_SQL)) {
            String hash = PasswordHashing.hash(password);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hash);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                boolean admin = rs.getBoolean("administrator");
                LOGGER.log(Level.INFO, "Found user: " + username + " with id: " + userId);
                return new AuthenticationResult(AuthenticationResult.Status.LOGIN_OK, userId, username, admin);
            } else {
                LOGGER.log(Level.INFO, "Did not find user: " + username);
                return new AuthenticationResult(AuthenticationResult.Status.INVALID);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error looking up the user in db", e);
            return new AuthenticationResult(AuthenticationResult.Status.UNAVAILABLE);
        }
    }

    public AuthenticationResult registerUser(final String username, final String password) {
        if (password == null || password.isEmpty()) {
            return new AuthenticationResult(AuthenticationResult.Status.INVALID);
        }
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_SQL)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                LOGGER.log(Level.INFO, "Found existing user: " + username + " with id: " + userId);
                return new AuthenticationResult(AuthenticationResult.Status.INVALID);
            } else {
                String hash = PasswordHashing.hash(password);
                int userId = insertUser(username, hash);
                LOGGER.log(Level.INFO, "Registered new user: " + username);
                return new AuthenticationResult(AuthenticationResult.Status.LOGIN_OK, userId, username, false);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error registering new user in db", e);
            return new AuthenticationResult(AuthenticationResult.Status.UNAVAILABLE);
        }
    }

    private int insertUser(final String userName, final String passwordHash) throws SQLException {

        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, passwordHash);
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                key = rs.getInt(1);
                LOGGER.log(Level.INFO, "Inserted user with index " + key);
            } else {
                LOGGER.log(Level.SEVERE, "Could not insert user");
            }
        }

        return key;
    }

    public void insertUserPbStats(final PersistentUserProblemStats userProblemStats) {

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_PB_STATS)) {
            preparedStatement.setInt(1, userProblemStats.getUserId());
            preparedStatement.setInt(2, userProblemStats.getProblemId());
            preparedStatement.setInt(3, userProblemStats.getTimeSpentMs());
            preparedStatement.setBoolean(4, userProblemStats.getCorrect());
            preparedStatement.executeUpdate();

            LOGGER.log(Level.INFO, "Inserted user pb stats");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the user pb stats in db", e);
        }
    }

    public List<PersistentUserProblemStats> getUserPbStats(int userId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_PB_STATS)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            List<PersistentUserProblemStats> result = new ArrayList<>();

            while (rs.next()) {

                if (rs.getInt("user_id") != userId) {
                    LOGGER.log(Level.SEVERE, "Invalid results for user pb stats");
                    return new ArrayList<>();
                }
                int problemId = rs.getInt("problem_id");
                Timestamp timestampAttempted = rs.getTimestamp("timestamp_attempted");
                Integer timeSpentMs = rs.getInt("time_spent_ms");
                Boolean correct = rs.getBoolean("correct");

                result.add(new PersistentUserProblemStats(userId, problemId, timestampAttempted, timeSpentMs, correct));

            }

            LOGGER.log(Level.INFO, "Found user pb stats: " + result);

            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the user pb stats", e);
            return new ArrayList<>();
        }
    }

    public void insertUserHighScore(final String name, final int score, final Integer userId, final String event) {

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_HIGHSCORE)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, score);
            SqlUtils.setInteger(preparedStatement, 3, userId);
            preparedStatement.setString(4, event);
            preparedStatement.executeUpdate();

            LOGGER.log(Level.INFO, "Inserted user high score");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the user high score in db", e);
        }
    }
}
