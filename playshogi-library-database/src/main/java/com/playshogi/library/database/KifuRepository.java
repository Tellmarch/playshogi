package com.playshogi.library.database;

import com.playshogi.library.database.models.PersistentGame;
import com.playshogi.library.database.models.PersistentKifu;
import com.playshogi.library.database.models.PersistentKifu.KifuType;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.record.GameRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KifuRepository {

    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    private static final String INSERT_KIFU = "INSERT INTO `playshogi`.`ps_kifu` (`name`, `author_id`, `usf`, " +
            "`type_id`)" + " VALUES (?, ?, ?,  ?);";

    private static final String INSERT_KIFU_POSITION = "INSERT INTO `playshogi`.`ps_kifupos` (`kifu_id`, " +
            "`position_id`) VALUES (?, ?) ON DUPLICATE KEY update kifu_id=kifu_id;";

    private static final String SELECT_KIFU_POSITION = "SELECT * FROM playshogi.ps_kifupos JOIN playshogi.ps_game ON " +
            "ps_kifupos.kifu_id = ps_game.kifu_id WHERE position_id = ? LIMIT 5";

    private static final String SELECT_KIFU_POSITION_GAMESET = "SELECT * FROM playshogi.ps_kifupos JOIN playshogi" +
            ".ps_game ON ps_kifupos.kifu_id = ps_game.kifu_id JOIN playshogi.ps_gamesetgame ON " +
            "ps_game.id = ps_gamesetgame.game_id WHERE position_id = ? AND gameset_id = ? LIMIT 5";

    private static final String SELECT_KIFU = "SELECT * FROM ps_kifu WHERE id = ?";
    private static final String DELETE_KIFU = "DELETE FROM ps_kifu WHERE id = ? AND author_id = ?";

    private static final String SELECT_USER_KIFUS = "SELECT id, name, author_id, create_time, update_time, type_id " +
            "FROM ps_kifu WHERE author_id = ? ORDER BY update_time DESC LIMIT 1000;";

    private static final String UPDATE_KIFU = "UPDATE ps_kifu SET usf = ? WHERE id = ? AND author_id = ?";

    private final DbConnection dbConnection;

    public KifuRepository(final DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public int saveKifu(final GameRecord gameRecord, final String name, final int authorId, final KifuType kifuType) {

        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_KIFU,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, authorId);
            preparedStatement.setString(3, UsfFormat.INSTANCE.write(gameRecord));
            preparedStatement.setInt(4, kifuType.getDbInt());
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                key = rs.getInt(1);
                LOGGER.log(Level.INFO, "Inserted kifu with index " + key);
            } else {
                LOGGER.log(Level.SEVERE, "Could not insert kifu");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the kifu in db", e);
        }

        return key;
    }

    public PersistentKifu getKifuById(final int kifuId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_KIFU)) {
            preparedStatement.setInt(1, kifuId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                LOGGER.log(Level.INFO, "Found kifu: " + rs.getString("name") + " with id: " + rs.getInt("id"));
                String name = rs.getString("name");
                int authorId = rs.getInt("author_id");
                String usfString = rs.getString("usf");
                int type = rs.getInt("type_id");
                KifuType kifuType = KifuType.fromDbInt(type);
                Date creationDate = rs.getDate("create_time");
                Date updateDate = rs.getDate("update_time");
                GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(usfString);

                return new PersistentKifu(kifuId, name, gameRecord, creationDate, updateDate, kifuType, authorId);
            } else {
                LOGGER.log(Level.INFO, "Did not find kifu: " + kifuId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the kifu in db", e);
            return null;
        }
    }

    public List<PersistentKifu> getUserKifus(final int userId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_KIFUS)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<PersistentKifu> kifus = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int type = rs.getInt("type_id");
                KifuType kifuType = KifuType.fromDbInt(type);
                Date creationDate = rs.getDate("create_time");
                Date updateDate = rs.getDate("update_time");

                kifus.add(new PersistentKifu(id, name, null, creationDate, updateDate, kifuType, userId));
            }
            LOGGER.log(Level.INFO, "Found " + kifus.size() + " kifus for user : " + userId);
            return kifus;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the kifus of user " + userId + " in db", e);
            return null;
        }
    }

    public boolean deleteKifuById(final int kifuId, final int userId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_KIFU)) {
            preparedStatement.setInt(1, kifuId);
            preparedStatement.setInt(2, userId);
            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "Deleted kifu: " + kifuId);
                return true;
            } else {
                LOGGER.log(Level.INFO, "Did not find kifu: " + kifuId + " for user " + userId);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the kifu in db", e);
            return false;
        }
    }

    public void saveKifuPosition(final int kifuId, final ShogiPosition position) {
        int lastPositionId = new PositionRepository(dbConnection).getOrSavePosition(position);
        saveKifuPosition(kifuId, lastPositionId);
    }

    public void saveKifuPosition(final int kifuId, final int positionId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_KIFU_POSITION)) {
            preparedStatement.setInt(1, kifuId);
            preparedStatement.setInt(2, positionId);
            if (preparedStatement.executeUpdate() != 1) {
                LOGGER.log(Level.SEVERE, "Could not insert kifu position");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the kifu in db", e);
        }
    }

    public List<PersistentGame> getGamesForPosition(final int positionId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_KIFU_POSITION)) {
            preparedStatement.setInt(1, positionId);
            ResultSet rs = preparedStatement.executeQuery();
            List<PersistentGame> result = new ArrayList<>();

            while (rs.next()) {

                int gameId = rs.getInt("ps_game.id");
                int kifuId = rs.getInt("ps_game.kifu_id");
                int senteId = rs.getInt("ps_game.sente_id");
                int goteId = rs.getInt("ps_game.gote_id");
                String senteName = rs.getString("ps_game.sente_name");
                String goteName = rs.getString("ps_game.gote_name");
                int venueId = rs.getInt("ps_game.venue");
                String description = rs.getString("ps_game.description");
                Date datePlayed = rs.getDate("ps_game.date_played");

                result.add(new PersistentGame(gameId, kifuId, senteId, goteId, senteName, goteName, datePlayed,
                        venueId, description));

            }

            LOGGER.log(Level.INFO, "Found kifus: " + result);

            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the kifus for position in db", e);
            return null;
        }
    }

    public List<PersistentGame> getGamesForPosition(final int positionId, final int gameSetId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_KIFU_POSITION_GAMESET)) {
            preparedStatement.setInt(1, positionId);
            preparedStatement.setInt(2, gameSetId);
            ResultSet rs = preparedStatement.executeQuery();
            List<PersistentGame> result = new ArrayList<>();

            while (rs.next()) {

                int gameId = rs.getInt("ps_game.id");
                int kifuId = rs.getInt("ps_game.kifu_id");
                int senteId = rs.getInt("ps_game.sente_id");
                int goteId = rs.getInt("ps_game.gote_id");
                String senteName = rs.getString("ps_game.sente_name");
                String goteName = rs.getString("ps_game.gote_name");
                int venueId = rs.getInt("ps_game.venue");
                String description = rs.getString("ps_game.description");
                Date datePlayed = rs.getDate("ps_game.date_played");

                result.add(new PersistentGame(gameId, kifuId, senteId, goteId, senteName, goteName, datePlayed,
                        venueId, description));

            }

            LOGGER.log(Level.INFO, "Found kifus: " + result);

            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the kifus for position in db", e);
            return null;
        }
    }

    public boolean updateKifu(final int kifuId, final int userId, final GameRecord gameRecord) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_KIFU)) {
            preparedStatement.setString(1, UsfFormat.INSTANCE.write(gameRecord));
            preparedStatement.setInt(2, kifuId);
            preparedStatement.setInt(3, userId);
            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "updated kifu: " + kifuId);
                return true;
            } else {
                LOGGER.log(Level.INFO, "Did not find kifu: " + kifuId + " for user " + userId);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating the kifu in db", e);
            return false;
        }
    }
}
