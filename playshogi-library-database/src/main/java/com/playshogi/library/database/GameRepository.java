package com.playshogi.library.database;

import com.playshogi.library.database.models.PersistentGame;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameRepository {

    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    private static final String INSERT_GAME = "INSERT INTO `playshogi`.`ps_game` "
            + "(`kifu_id`, `sente_id`, `gote_id`, `sente_name`, `gote_name`, `date_played`, `venue`, `description`)" + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String SELECT_GAME = "SELECT * FROM ps_game WHERE id = ?";
    private static final String DELETE_GAME = "DELETE FROM ps_game WHERE id = ?";

    private static final String SELECT_GAMES_FROM_GAMESET = "SELECT * FROM playshogi.ps_gamesetgame join playshogi" +
            ".ps_game on ps_game.id = game_id WHERE gameset_id = ?;";

    private final DbConnection dbConnection;

    public GameRepository(final DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public int saveGame(final int kifuId, final Integer senteId, final Integer goteId, final String senteName,
                        final String goteName, final Date datePlayed,
                        final int venueId, final String description) {

        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GAME,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, kifuId);
            setIntParameterOrNull(senteId, preparedStatement, 2);
            setIntParameterOrNull(goteId, preparedStatement, 3);
            preparedStatement.setString(4, senteName);
            preparedStatement.setString(5, goteName);
            preparedStatement.setDate(6, datePlayed == null ? null : new java.sql.Date(datePlayed.getTime()));
            preparedStatement.setInt(7, venueId);
            preparedStatement.setString(8, description);
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                key = rs.getInt(1);
                LOGGER.log(Level.INFO, "Inserted game with index " + key);
            } else {
                LOGGER.log(Level.SEVERE, "Could not insert game");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the game in db", e);
        }

        return key;
    }

    private void setIntParameterOrNull(final Integer value, final PreparedStatement preparedStatement,
                                       final int parameterIndex) throws SQLException {
        if (value != null) {
            preparedStatement.setInt(parameterIndex, value);
        } else {
            preparedStatement.setNull(parameterIndex, Types.INTEGER);
        }
    }

    public PersistentGame getGameById(final int gameId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GAME)) {
            preparedStatement.setInt(1, gameId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                LOGGER.log(Level.INFO, "Found game with id: " + rs.getInt("id"));

                int kifuId = rs.getInt("kifu_id");
                int senteId = rs.getInt("sente_id");
                int goteId = rs.getInt("gote_id");
                String senteName = rs.getString("sente_name");
                String goteName = rs.getString("gote_name");
                int venueId = rs.getInt("venue");
                String description = rs.getString("description");
                Date datePlayed = rs.getDate("date_played");

                return new PersistentGame(gameId, kifuId, senteId, goteId, senteName, goteName, datePlayed, venueId,
                        description);
            } else {
                LOGGER.log(Level.INFO, "Did not find game: " + gameId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the game in db", e);
            return null;
        }
    }

    public List<PersistentGame> getGamesFromGameSet(final int gameSetId) {
        return getGamesFromGameSet(gameSetId, false);
    }

    public List<PersistentGame> getGamesFromGameSet(final int gameSetId, final boolean reducedLogging) {
        ArrayList<PersistentGame> games = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GAMES_FROM_GAMESET)) {
            preparedStatement.setInt(1, gameSetId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if (!reducedLogging)
                    LOGGER.log(Level.INFO, "Found game with id: " + rs.getInt("game_id"));

                int kifuId = rs.getInt("kifu_id");
                int gameId = rs.getInt("game_id");
                int senteId = rs.getInt("sente_id");
                int goteId = rs.getInt("gote_id");
                String senteName = rs.getString("sente_name");
                String goteName = rs.getString("gote_name");
                int venueId = rs.getInt("venue");
                String description = rs.getString("description");
                Date datePlayed = rs.getDate("date_played");

                games.add(new PersistentGame(gameId, kifuId, senteId, goteId, senteName, goteName, datePlayed, venueId,
                        description));
            }
            return games;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the gameset games in db", e);
            return null;
        }
    }

    public void deleteGameById(final int gameId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_GAME)) {
            preparedStatement.setInt(1, gameId);
            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "Deleted kifu: " + gameId);
            } else {
                LOGGER.log(Level.INFO, "Did not find kifu: " + gameId);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the kifu in db", e);
        }
    }

}
