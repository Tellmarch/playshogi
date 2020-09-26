package com.playshogi.library.database;

import com.playshogi.library.database.models.PersistentGameSet;
import com.playshogi.library.database.models.PersistentGameSetMove;
import com.playshogi.library.database.models.PersistentGameSetPos;
import com.playshogi.library.database.models.PersistentKifu.KifuType;
import com.playshogi.library.models.Move;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.models.record.GameResult;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameSetRepository {

    private static final Logger LOGGER = Logger.getLogger(GameSetRepository.class.getName());

    private static final String INSERT_GAMESET = "INSERT INTO `playshogi`.`ps_gameset` (`name`, `description`)" + " " +
            "VALUES (?, ?);";
    private static final String SELECT_GAMESET = "SELECT * FROM `playshogi`.`ps_gameset` WHERE id = ?";
    private static final String SELECT_ALL_GAMESET = "SELECT * FROM `playshogi`.`ps_gameset` LIMIT 1000";
    private static final String DELETE_GAMESET = "DELETE FROM `playshogi`.`ps_gameset` WHERE id = ?";

    private static final String INSERT_GAMESET_GAME = "INSERT INTO `playshogi`.`ps_gamesetgame`" +
            "(`gameset_id`,`game_id`) VALUES (?,?);";

    private static final String INCREMENT_GAMESET_POSITION_SENTE_WIN = "INSERT INTO `playshogi`.`ps_gamesetpos` " +
            "(`position_id`, `gameset_id`, `num_total`, `num_sente_win`, `num_gote_win`)"
            + " VALUES (?, ?, 1, 1, 0) ON DUPLICATE KEY UPDATE num_sente_win=num_sente_win+1,num_total=num_total+1;";

    private static final String INCREMENT_GAMESET_POSITION_GOTE_WIN = "INSERT INTO `playshogi`.`ps_gamesetpos` " +
            "(`position_id`, `gameset_id`, `num_total`, `num_sente_win`, `num_gote_win`)"
            + " VALUES (?, ?, 1, 0, 1) ON DUPLICATE KEY UPDATE num_gote_win=num_gote_win+1,num_total=num_total+1;";

    private static final String INCREMENT_GAMESET_POSITION_OTHER = "INSERT INTO `playshogi`.`ps_gamesetpos` " +
            "(`position_id`, `gameset_id`, `num_total`, `num_sente_win`, `num_gote_win`)"
            + " VALUES (?, ?, 1, 0, 0) ON DUPLICATE KEY UPDATE num_total=num_total+1;";

    private static final String SELECT_GAMESET_POSITION = "SELECT * FROM `playshogi`.`ps_gamesetpos` WHERE " +
            "position_id = ? AND gameset_id = ?";

    private static final String INCREMENT_GAMESET_MOVE = "INSERT INTO `playshogi`.`ps_gamesetmove` (`position_id`, " +
            "`move`, `new_position_id`, `gameset_id`, `num_total`)"
            + " VALUES (?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE num_total=num_total+1;";

    private static final String SELECT_GAMESET_MOVES = "SELECT * FROM ps_gamesetmove"
            + " LEFT JOIN ps_gamesetpos ON ps_gamesetmove.new_position_id = ps_gamesetpos.position_id AND " +
            "ps_gamesetmove.gameset_id = ps_gamesetpos.gameset_id"
            + " WHERE ps_gamesetmove.position_id = ? AND ps_gamesetmove.gameset_id = ? ORDER BY ps_gamesetmove" +
            ".num_total DESC";

    private final DbConnection dbConnection;

    public GameSetRepository(final DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public int saveGameSet(final String name, final String description) {

        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GAMESET,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                key = rs.getInt(1);
                LOGGER.log(Level.INFO, "Inserted gameset with index " + key);
            } else {
                LOGGER.log(Level.SEVERE, "Could not insert gameset");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the gameset in db", e);
        }

        return key;
    }

    public void addGameSetGameRecord(final int gameSetId, final int gameId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GAMESET_GAME)) {
            preparedStatement.setInt(1, gameSetId);
            preparedStatement.setInt(2, gameId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the gamesetgame in db", e);
        }
    }

    public PersistentGameSet getGameSetById(final int gameSetId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GAMESET)) {
            preparedStatement.setInt(1, gameSetId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                LOGGER.log(Level.INFO, "Found gameset: " + rs.getString("name") + " with id: " + rs.getInt("id"));
                String name = rs.getString("name");
                String description = rs.getString("description");
                PersistentGameSet.Visibility visibility = PersistentGameSet.Visibility.values()[rs.getInt("visibility"
                )];
                Integer ownerId = SqlUtils.getInteger(rs, "owner_user_id");

                return new PersistentGameSet(gameSetId, name, description, visibility, ownerId);
            } else {
                LOGGER.log(Level.INFO, "Did not find gameset: " + gameSetId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the gameset in db", e);
            return null;
        }
    }

    public List<PersistentGameSet> getAllGameSets() {
        List<PersistentGameSet> result = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_GAMESET)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                String description = rs.getString("description");
                PersistentGameSet.Visibility visibility = PersistentGameSet.Visibility.values()[rs.getInt("visibility"
                )];
                Integer ownerId = SqlUtils.getInteger(rs, "owner_user_id");

                result.add(new PersistentGameSet(id, name, description, visibility, ownerId));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving gamesets in db", e);
        }
        return result;
    }

    public void deleteGamesetById(final int gameSetId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_GAMESET)) {
            preparedStatement.setInt(1, gameSetId);
            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "Deleted gameset: " + gameSetId);
            } else {
                LOGGER.log(Level.INFO, "Did not find gameset: " + gameSetId);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting up the gameset in db", e);
        }
    }

    public void addGameToGameSet(final GameRecord gameRecord, final int gameSetId, final int venueId,
                                 final String gameName, final int authorId) {
        PositionRepository rep = new PositionRepository(dbConnection);
        KifuRepository kifuRep = new KifuRepository(dbConnection);
        GameRepository gameRep = new GameRepository(dbConnection);

        int kifuId = kifuRep.saveKifu(gameRecord, gameName, authorId, KifuType.GAME);

        int gameId = gameRep.saveGame(kifuId, null, null, gameRecord.getGameInformation().getSente(),
                gameRecord.getGameInformation().getGote(),
                parseDate(gameRecord.getGameInformation().getDate()), venueId, gameName);

        addGameSetGameRecord(gameSetId, gameId);

        boolean senteWin = gameRecord.getGameResult() == GameResult.SENTE_WIN;
        boolean goteWin = gameRecord.getGameResult() == GameResult.GOTE_WIN;

        GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(new ShogiRulesEngine(),
                gameRecord.getGameTree(),
                ShogiInitialPositionFactory.createInitialPosition());

        int lastPositionId = rep.getOrSavePosition(gameNavigation.getPosition());

        kifuRep.saveKifuPosition(kifuId, lastPositionId);
        incrementGameSetPosition(gameSetId, lastPositionId, senteWin, goteWin);

        while (gameNavigation.canMoveForward()) {
            Move move = gameNavigation.getMainVariationMove();

            gameNavigation.moveForward();

            int positionId = rep.getOrSavePosition(gameNavigation.getPosition());

            kifuRep.saveKifuPosition(kifuId, positionId);
            incrementGameSetPosition(gameSetId, positionId, senteWin, goteWin);
            incrementGameSetMove(gameSetId, lastPositionId, UsfMoveConverter.toUsfString((ShogiMove) move), positionId);

            lastPositionId = positionId;
        }

    }

    private Date parseDate(final String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            return simpleDateFormat.parse(date);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Couldn't parse date: " + date);
        }

        return null;
    }

    public void incrementGameSetPosition(final int gameSetId, final int positionId, final boolean senteWin,
                                         final boolean goteWin) {

        String statement = senteWin ? INCREMENT_GAMESET_POSITION_SENTE_WIN : (goteWin ?
                INCREMENT_GAMESET_POSITION_GOTE_WIN : INCREMENT_GAMESET_POSITION_OTHER);

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setInt(1, positionId);
            preparedStatement.setInt(2, gameSetId);
            int updateResult = preparedStatement.executeUpdate();

            if (updateResult != 1 && updateResult != 2) {
                LOGGER.log(Level.SEVERE, "Could not insert gameset position");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting the gameset position in db", e);
        }

    }

    public void incrementGameSetMove(final int gameSetId, final int positionId, final String moveUsf,
                                     final int newPositionId) {

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INCREMENT_GAMESET_MOVE)) {
            preparedStatement.setInt(1, positionId);
            preparedStatement.setString(2, moveUsf);
            preparedStatement.setInt(3, newPositionId);
            preparedStatement.setInt(4, gameSetId);
            int updateResult = preparedStatement.executeUpdate();

            if (updateResult != 1 && updateResult != 2) {
                LOGGER.log(Level.SEVERE, "Could not insert gameset move");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting the gameset move in db", e);
        }

    }

    public PersistentGameSetPos getGameSetPositionStats(final String sfen, final int gameSetId) {
        int positionId = new PositionRepository(dbConnection).getPositionIdBySfen(sfen);
        return getGameSetPositionStats(positionId, gameSetId);
    }

    public PersistentGameSetPos getGameSetPositionStats(final int positionId, final int gameSetId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GAMESET_POSITION)) {
            preparedStatement.setInt(1, positionId);
            preparedStatement.setInt(2, gameSetId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("num_total");
                int senteWins = rs.getInt("num_sente_win");
                int goteWins = rs.getInt("num_gote_win");

                PersistentGameSetPos persistentGameSetPos = new PersistentGameSetPos(positionId, gameSetId, total,
                        senteWins, goteWins);

                LOGGER.log(Level.INFO, "Found position stats: " + persistentGameSetPos);

                return persistentGameSetPos;
            } else {
                LOGGER.log(Level.INFO, "Did not find gameset: " + gameSetId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the gameset in db", e);
            return null;
        }
    }

    public List<PersistentGameSetMove> getGameSetPositionMoveStats(final int positionId, final int gameSetId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GAMESET_MOVES)) {
            preparedStatement.setInt(1, positionId);
            preparedStatement.setInt(2, gameSetId);
            ResultSet rs = preparedStatement.executeQuery();

            ArrayList<PersistentGameSetMove> result = new ArrayList<>();

            while (rs.next()) {
                int total = rs.getInt("ps_gamesetmove.num_total");
                String move = rs.getString("move");
                int posTotal = rs.getInt("ps_gamesetpos.num_total");
                int senteWins = rs.getInt("num_sente_win");
                int goteWins = rs.getInt("num_gote_win");
                int newPositionId = rs.getInt("new_position_id");

                LOGGER.log(Level.INFO, "Found position move: " + total);

                result.add(new PersistentGameSetMove(move, total, positionId, newPositionId, gameSetId, posTotal,
                        senteWins, goteWins));
            }

            return result;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the gameset in db", e);
            return null;
        }
    }

}
