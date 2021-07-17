package com.playshogi.library.database;

import com.playshogi.library.database.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepository {

    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    private static final String FIND_SQL = "SELECT * FROM ps_user WHERE UPPER(username) = UPPER(?)";
    private static final String LOGIN_SQL = "SELECT * FROM ps_user WHERE UPPER(username) = UPPER(?) AND password_hash" +
            " = ? ";
    private static final String INSERT_USER = "INSERT INTO `playshogi`.`ps_user` "
            + "(`username`, `password_hash`)" + " VALUES ( ?, ?);";

    private static final String INSERT_USER_PB_STATS = "INSERT INTO `playshogi`.`ps_userpbstats` "
            + "(`user_id`, `problem_id`, `time_spent_ms`, `correct`)" + " VALUES ( ?, ?, ?, ?);";

    private static final String INSERT_USER_PBSET_STATS = "INSERT INTO `playshogi`.`ps_userpbsetstats` "
            + "(`user_id`, `problemset_id`, `time_spent_ms`, `complete`, `solved`)" + " VALUES ( ?, ?, ?, ?, ?);";

    private static final String GET_USER_PB_STATS = "SELECT * from playshogi.ps_userpbstats WHERE  user_id = ? ORDER " +
            "BY timestamp_attempted DESC;";

    private static final String GET_USER_DETAILS = "SELECT * from playshogi.ps_user WHERE id = ?";

    private static final String INSERT_USER_HIGHSCORE = "INSERT INTO `playshogi`.`ps_highscore` "
            + "(`name`, `score`, `user_id`, `event`)" + " VALUES ( ?, ?, ?, ?);";

    private static final String GET_USER_HIGHSCORES = "SELECT name, max(score) as score FROM ps_highscore WHERE event" +
            " = ? GROUP BY name ORDER BY score DESC LIMIT 20;";

    private static final String GET_COLLECTION_HIGHSCORES = "SELECT user_id, min(time_spent_ms) as time_spent_ms" +
            " FROM ps_userpbsetstats" +
            " WHERE problemset_id = ? AND complete = 1 GROUP BY user_id ORDER BY time_spent_ms ASC LIMIT 3;";

    private static final String GET_COLLECTION_USER_HIGHSCORE = "SELECT * FROM ps_userpbsetstats" +
            " WHERE problemset_id = ? AND user_id = ? AND complete = 1 ORDER BY time_spent_ms ASC LIMIT 1;";

    private static final String GET_COLLECTION_HIGHSCORES_WITH_NAMES = "SELECT username, min(time_spent_ms) as " +
            "time_spent_ms FROM ps_userpbsetstats JOIN ps_user u ON (user_id = u.id) WHERE problemset_id = ? AND " +
            "complete = 1 GROUP BY username ORDER BY time_spent_ms ASC LIMIT 3;";

    private static final String UPDATE_USER_LESSON_PROGRESS = "INSERT INTO `playshogi`.`ps_userlessonsprogress` "
            + "(`user_id`, `lesson_id`, `time_spent_ms`, `complete`, `percentage`, `rating`)" +
            " VALUES ( ?, ?, ?, ?, ?, ?)" +
            "ON DUPLICATE KEY UPDATE " +
            "time_spent_ms = GREATEST(time_spent_ms, ?), " +
            "complete = GREATEST(complete, ?), " +
            "percentage = GREATEST(percentage, ?), " +
            "rating = IFNULL(?, rating);";

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
                String trueUsername = rs.getString("username");
                LOGGER.log(Level.INFO, "Found user: " + trueUsername + " with id: " + userId);
                return new AuthenticationResult(AuthenticationResult.Status.LOGIN_OK, userId, trueUsername, admin);
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

    public void insertUserPbSetStats(final PersistentUserProblemSetStats userProblemStats) {

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_PBSET_STATS)) {
            preparedStatement.setInt(1, userProblemStats.getUserId());
            preparedStatement.setInt(2, userProblemStats.getCollectionId());
            preparedStatement.setInt(3, userProblemStats.getTimeSpentMs());
            preparedStatement.setBoolean(4, userProblemStats.getComplete());
            preparedStatement.setInt(5, userProblemStats.getSolved());
            preparedStatement.executeUpdate();

            LOGGER.log(Level.INFO, "Inserted user pbset stats");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the user pbset stats in db", e);
        }
    }

    public List<PersistentUserProblemStats> getUserPbStats(final int userId) {
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


    public List<PersistentHighScore> getHighScoresForEvent(final String event) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_HIGHSCORES)) {
            preparedStatement.setString(1, event);
            ResultSet rs = preparedStatement.executeQuery();
            List<PersistentHighScore> result = new ArrayList<>();

            while (rs.next()) {
                int score = rs.getInt("score");
                String name = rs.getString("name");
                result.add(new PersistentHighScore(0, null, score, null, name, null));

            }

            LOGGER.log(Level.INFO, "Found high scores: " + result);

            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the high scores", e);
            return new ArrayList<>();
        }
    }

    public List<PersistentUserProblemSetStats> getCollectionHighScores(final int collectionId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_COLLECTION_HIGHSCORES_WITH_NAMES)) {
            preparedStatement.setInt(1, collectionId);
            ResultSet rs = preparedStatement.executeQuery();
            List<PersistentUserProblemSetStats> result = new ArrayList<>();

            while (rs.next()) {
                int time_spent_ms = rs.getInt("time_spent_ms");
                String username = rs.getString("username");
                result.add(new PersistentUserProblemSetStats(0, username, collectionId, null, time_spent_ms, true, 0));
            }

            LOGGER.log(Level.INFO, "Found high scores: " + result);

            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the high scores", e);
            return new ArrayList<>();
        }
    }

    public PersistentUser getUserById(final int userId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_DETAILS)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                LOGGER.log(Level.INFO, "Found user with id: " + rs.getInt("id"));

                return new PersistentUser(rs.getInt("id"), rs.getString("username"), rs.getBoolean("administrator"));
            } else {
                LOGGER.log(Level.INFO, "Did not find user: " + userId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the user in db", e);
            return null;
        }
    }

    public PersistentUserProblemSetStats getCollectionHighScoreForUser(final int collectionId, final int userId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_COLLECTION_USER_HIGHSCORE)) {
            preparedStatement.setInt(1, collectionId);
            preparedStatement.setInt(2, userId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int time_spent_ms = rs.getInt("time_spent_ms");
                int solved = rs.getInt("solved");
                return new PersistentUserProblemSetStats(userId, null, collectionId, null, time_spent_ms, true, solved);
            } else {
                LOGGER.log(Level.INFO, "Did not find score for user: " + userId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the user in db", e);
            return null;
        }
    }

    public void insertUserLessonProgress(final PersistentUserLessonProgress userLessonProgress) {

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_LESSON_PROGRESS)) {
            // First set of values for the insert
            preparedStatement.setInt(1, userLessonProgress.getUserId());
            preparedStatement.setInt(2, userLessonProgress.getLessonId());
            SqlUtils.setInteger(preparedStatement, 3, userLessonProgress.getTimeSpentMs());
            preparedStatement.setBoolean(4, userLessonProgress.getComplete());
            preparedStatement.setInt(5, userLessonProgress.getPercentage());
            SqlUtils.setInteger(preparedStatement, 6, userLessonProgress.getRating());

            // Second set of values for the update
            SqlUtils.setInteger(preparedStatement, 7, userLessonProgress.getTimeSpentMs());
            preparedStatement.setBoolean(8, userLessonProgress.getComplete());
            preparedStatement.setInt(9, userLessonProgress.getPercentage());
            SqlUtils.setInteger(preparedStatement, 10, userLessonProgress.getRating());
            preparedStatement.executeUpdate();

            LOGGER.log(Level.INFO, "Inserted PersistentUserLessonProgress");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the PersistentUserLessonProgress in db", e);
        }
    }
}
