package com.playshogi.library.database;

import com.playshogi.library.database.models.PersistentGame;
import com.playshogi.library.database.models.PersistentKifu;
import com.playshogi.library.database.models.PersistentKifu.KifuType;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;

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

    private static final String SELECT_KIFU_POSITION = "SELECT * FROM ps_kifupos LEFT JOIN ps_game ON ps_kifupos" +
            ".kifu_id = ps_game.kifu_id WHERE position_id = ? LIMIT 10";

    private static final String SELECT_KIFU = "SELECT * FROM ps_kifu WHERE id = ?";
    private static final String DELETE_KIFU = "DELETE FROM ps_kifu WHERE id = ?";

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
                GameRecord gameRecord = UsfFormat.INSTANCE.read(usfString);

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

    public void deleteKifuById(final int kifuId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_KIFU)) {
            preparedStatement.setInt(1, kifuId);
            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "Deleted kifu: " + kifuId);
            } else {
                LOGGER.log(Level.INFO, "Did not find kifu: " + kifuId);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the kifu in db", e);
        }
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

}
