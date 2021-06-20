package com.playshogi.library.database;

import com.playshogi.library.database.models.*;
import com.playshogi.library.database.models.PersistentKifu.KifuType;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.library.shogi.models.record.GameResult;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.playshogi.library.database.SqlUtils.setInteger;

public class GameSetRepository {

    private static final Logger LOGGER = Logger.getLogger(GameSetRepository.class.getName());

    private static final String INSERT_GAMESET = "INSERT INTO `playshogi`.`ps_gameset` (`name`, `description`, " +
            "`visibility`, `owner_user_id`) VALUES (?, ?, ?, ?);";
    private static final String SELECT_GAMESET = "SELECT * FROM `playshogi`.`ps_gameset` WHERE id = ?";

    private static final String SELECT_GAMESETS_FOR_USER = "SELECT * FROM `playshogi`.`ps_gameset` WHERE " +
            "owner_user_id = ? LIMIT 1000";
    private static final String SELECT_PUBLIC_GAMESETS = "SELECT * FROM `playshogi`.`ps_gameset` WHERE visibility = 2" +
            " LIMIT 1000";
    private static final String SELECT_ALL_GAMESET = "SELECT * FROM `playshogi`.`ps_gameset` LIMIT 1000";
    private static final String DELETE_GAMESET = "DELETE FROM `playshogi`.`ps_gameset` WHERE id = ? AND owner_user_id" +
            " = ?";

    private static final String UPDATE_GAMESET = "UPDATE `playshogi`.`ps_gameset` " +
            "SET `name` = ?, `description` = ?, `visibility` = ? WHERE `id` = ? AND `owner_user_id` = ?;";

    private static final String INSERT_GAMESET_GAME = "INSERT INTO `playshogi`.`ps_gamesetgame`" +
            "(`gameset_id`,`game_id`) VALUES (?,?);";

    private static final String DELETE_GAMESET_GAME = "DELETE ps_gamesetgame FROM playshogi.ps_gamesetgame JOIN " +
            "playshogi.ps_gameset on id=gameset_id WHERE gameset_id = ? and game_id=? and owner_user_id =?;";

    private static final String INCREMENT_GAMESET_POSITION_SENTE_WIN = "INSERT INTO `playshogi`.`ps_gamesetpos` " +
            "(`position_id`, `gameset_id`, `num_total`, `num_sente_win`, `num_gote_win`)"
            + " VALUES (?, ?, 1, 1, 0) ON DUPLICATE KEY UPDATE num_sente_win=num_sente_win+1,num_total=num_total+1;";

    private static final String INCREMENT_GAMESET_POSITION_GOTE_WIN = "INSERT INTO `playshogi`.`ps_gamesetpos` " +
            "(`position_id`, `gameset_id`, `num_total`, `num_sente_win`, `num_gote_win`)"
            + " VALUES (?, ?, 1, 0, 1) ON DUPLICATE KEY UPDATE num_gote_win=num_gote_win+1,num_total=num_total+1;";

    private static final String INCREMENT_GAMESET_POSITION_OTHER = "INSERT INTO `playshogi`.`ps_gamesetpos` " +
            "(`position_id`, `gameset_id`, `num_total`, `num_sente_win`, `num_gote_win`)"
            + " VALUES (?, ?, 1, 0, 0) ON DUPLICATE KEY UPDATE num_total=num_total+1;";

    private static final String DECREMENT_GAMESET_POSITION_SENTE_WIN = "INSERT INTO `playshogi`.`ps_gamesetpos` " +
            "(`position_id`, `gameset_id`, `num_total`, `num_sente_win`, `num_gote_win`)"
            + " VALUES (?, ?, 0, 0, 0) ON DUPLICATE KEY UPDATE num_sente_win=num_sente_win-1,num_total=num_total-1;";

    private static final String DECREMENT_GAMESET_POSITION_GOTE_WIN = "INSERT INTO `playshogi`.`ps_gamesetpos` " +
            "(`position_id`, `gameset_id`, `num_total`, `num_sente_win`, `num_gote_win`)"
            + " VALUES (?, ?, 0, 0, 0) ON DUPLICATE KEY UPDATE num_gote_win=num_gote_win-1,num_total=num_total-1;";

    private static final String DECREMENT_GAMESET_POSITION_OTHER = "INSERT INTO `playshogi`.`ps_gamesetpos` " +
            "(`position_id`, `gameset_id`, `num_total`, `num_sente_win`, `num_gote_win`)"
            + " VALUES (?, ?, 0, 0, 0) ON DUPLICATE KEY UPDATE num_total=num_total-1;";

    private static final String SELECT_GAMESET_POSITION = "SELECT * FROM `playshogi`.`ps_gamesetpos` WHERE " +
            "position_id = ? AND gameset_id = ?";

    private static final String INCREMENT_GAMESET_MOVE = "INSERT INTO `playshogi`.`ps_gamesetmove` (`position_id`, " +
            "`move`, `new_position_id`, `gameset_id`, `num_total`)"
            + " VALUES (?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE num_total=num_total+1;";

    private static final String DECREMENT_GAMESET_MOVE = "INSERT INTO `playshogi`.`ps_gamesetmove` (`position_id`, " +
            "`move`, `new_position_id`, `gameset_id`, `num_total`)"
            + " VALUES (?, ?, ?, ?, 0) ON DUPLICATE KEY UPDATE num_total=num_total-1;";


    private static final String SELECT_GAMESET_MOVES = "SELECT * FROM ps_gamesetmove"
            + " LEFT JOIN ps_gamesetpos ON ps_gamesetmove.new_position_id = ps_gamesetpos.position_id AND " +
            "ps_gamesetmove.gameset_id = ps_gamesetpos.gameset_id"
            + " WHERE ps_gamesetmove.position_id = ? AND ps_gamesetmove.gameset_id = ? ORDER BY ps_gamesetmove" +
            ".num_total DESC";

    private static final String SELECT_COUNT_GAMES_FROM_GAMESET = "SELECT COUNT(*) as num_games" +
            " FROM playshogi.ps_gamesetgame WHERE gameset_id = ?;";

    private final DbConnection dbConnection;

    private final PositionRepository positionRepository;
    private final KifuRepository kifuRepository;
    private final GameRepository gameRepository;

    public GameSetRepository(final DbConnection dbConnection) {
        this.dbConnection = dbConnection;
        positionRepository = new PositionRepository(dbConnection);
        kifuRepository = new KifuRepository(dbConnection);
        gameRepository = new GameRepository(dbConnection);
    }

    public int saveGameSet(final String name, final String description, final Visibility visibility
            , final Integer ownerId) {

        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GAMESET,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, visibility.ordinal());
            setInteger(preparedStatement, 4, ownerId);
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

    public void updateGameSet(final int id, final String name, final String description,
                              final Visibility visibility, final Integer ownerId) {

        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GAMESET)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, visibility.ordinal());
            preparedStatement.setInt(4, id);
            setInteger(preparedStatement, 5, ownerId);
            int res = preparedStatement.executeUpdate();

            if (res == 1) {
                LOGGER.log(Level.INFO, "Updated gameset with index " + key);
            } else {
                LOGGER.log(Level.SEVERE, "Could not update gameset (res = " + res + ")");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating the gameset in db", e);
        }
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
                Visibility visibility = Visibility.values()[rs.getInt("visibility"
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
                Visibility visibility = Visibility.values()[rs.getInt("visibility"
                )];
                Integer ownerId = SqlUtils.getInteger(rs, "owner_user_id");

                result.add(new PersistentGameSet(id, name, description, visibility, ownerId));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving gamesets in db", e);
        }
        return result;
    }

    public List<PersistentGameSet> getAllPublicGameSets() {
        List<PersistentGameSet> result = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PUBLIC_GAMESETS)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                String description = rs.getString("description");
                Visibility visibility = Visibility.values()[rs.getInt("visibility"
                )];
                Integer ownerId = SqlUtils.getInteger(rs, "owner_user_id");

                result.add(new PersistentGameSet(id, name, description, visibility, ownerId));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving public gamesets in db", e);
        }
        return result;
    }

    public List<PersistentGameSet> getGameSetsForUser(int userId) {
        List<PersistentGameSet> result = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GAMESETS_FOR_USER)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                String description = rs.getString("description");
                Visibility visibility = Visibility.values()[rs.getInt("visibility"
                )];
                Integer ownerId = SqlUtils.getInteger(rs, "owner_user_id");

                result.add(new PersistentGameSet(id, name, description, visibility, ownerId));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving gamesets for user in db", e);
        }
        return result;
    }


    public boolean deleteGamesetById(final int gameSetId, final int userId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_GAMESET)) {
            preparedStatement.setInt(1, gameSetId);
            preparedStatement.setInt(2, userId);
            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "Deleted gameset: " + gameSetId);
                return true;
            } else {
                LOGGER.log(Level.INFO, "Did not find gameset: " + gameSetId);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting up the gameset in db", e);
            return false;
        }
    }

    public boolean deleteGameFromGameSet(final int gameId, final int gameSetId, final int userId) {
        boolean result = deleteFromGameSetGameTable(gameId, gameSetId, userId);

        if (!result) { // No permission
            return false;
        }

        PersistentGame game = gameRepository.getGameById(gameId);

        if (game == null) {
            return true;
        }

        PersistentKifu kifu = kifuRepository.getKifuById(game.getKifuId());

        if (kifu == null) {
            return true;
        }

        GameRecord gameRecord = kifu.getKifu();

        GameNavigation gameNavigation = new GameNavigation(new ShogiRulesEngine(), gameRecord.getGameTree());

        boolean senteWin = gameRecord.getGameResult() == GameResult.BLACK_WIN;
        boolean goteWin = gameRecord.getGameResult() == GameResult.WHITE_WIN;

        int lastPositionId = positionRepository.getOrSavePosition(gameNavigation.getPosition());

        decrementGameSetPosition(gameSetId, lastPositionId, senteWin, goteWin);

        while (gameNavigation.canMoveForward()) {
            Move move = gameNavigation.getMainVariationMove();

            gameNavigation.moveForward();

            int positionId = positionRepository.getOrSavePosition(gameNavigation.getPosition());

            decrementGameSetPosition(gameSetId, positionId, senteWin, goteWin);
            decrementGameSetMove(gameSetId, lastPositionId, UsfMoveConverter.toUsfString((ShogiMove) move), positionId);

            lastPositionId = positionId;
        }

        return true;
    }

    private boolean deleteFromGameSetGameTable(final int gameId, final int gameSetId, final int userId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_GAMESET_GAME)) {
            preparedStatement.setInt(1, gameSetId);
            preparedStatement.setInt(2, gameId);
            preparedStatement.setInt(3, userId);
            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "Deleted game from gameset: " + gameId + " " + gameSetId);
                return true;
            } else {
                LOGGER.log(Level.INFO, "Could not delete game: " + gameId + " " + gameSetId);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting up the game from gameset in db", e);
            return false;
        }
    }

    public boolean saveKifuAndGameToGameSet(final GameRecord gameRecord, final int gameSetId, final int venueId,
                                            final String gameName, final int authorId) {
        int kifuId = kifuRepository.saveKifu(gameRecord, gameName, authorId, KifuType.GAME);

        if (kifuId == -1) {
            return false;
        }

        return saveGameFromKifuAndAddToGameSet(gameSetId, kifuId, venueId);
    }

    public boolean saveGameFromKifuAndAddToGameSet(final int gameSetId, final int kifuId, final int venueId) {

        PersistentKifu kifu = kifuRepository.getKifuById(kifuId);
        GameRecord gameRecord = kifu.getKifu();
        String gameName = kifu.getName();

        int gameId = gameRepository.saveGame(kifuId, null, null, gameRecord.getGameInformation().getBlack(),
                gameRecord.getGameInformation().getWhite(),
                parseDate(gameRecord.getGameInformation().getDate()), venueId, gameName);

        addGameSetGameRecord(gameSetId, gameId);

        boolean senteWin = gameRecord.getGameResult() == GameResult.BLACK_WIN;
        boolean goteWin = gameRecord.getGameResult() == GameResult.WHITE_WIN;

        GameNavigation gameNavigation = new GameNavigation(new ShogiRulesEngine(), gameRecord.getGameTree());

        int lastPositionId = positionRepository.getOrSavePosition(gameNavigation.getPosition());

        kifuRepository.saveKifuPosition(kifuId, lastPositionId);
        incrementGameSetPosition(gameSetId, lastPositionId, senteWin, goteWin);

        while (gameNavigation.canMoveForward()) {
            Move move = gameNavigation.getMainVariationMove();

            gameNavigation.moveForward();

            int positionId = positionRepository.getOrSavePosition(gameNavigation.getPosition());

            kifuRepository.saveKifuPosition(kifuId, positionId);
            incrementGameSetPosition(gameSetId, positionId, senteWin, goteWin);
            incrementGameSetMove(gameSetId, lastPositionId, UsfMoveConverter.toUsfString((ShogiMove) move), positionId);

            lastPositionId = positionId;
        }

        return true;
    }

    private static Pattern DATE_PATTERN_1 = Pattern.compile("^\\d{4}/\\d{2}/\\d{2}.*");
    private static Pattern DATE_PATTERN_2 = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}.*");
    private static Pattern DATE_PATTERN_3 = Pattern.compile("^\\d{8}.*");
    private static Pattern DATE_PATTERN_4 = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}.*");

    public static Date parseDate(final String date) {

        if (DATE_PATTERN_1.matcher(date).matches()) {
            try {
                return new SimpleDateFormat("yyyy/MM/dd").parse(date);
            } catch (Exception ignored) {
            }
        } else if (DATE_PATTERN_2.matcher(date).matches()) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(date);
            } catch (Exception ignored) {
            }
        } else if (DATE_PATTERN_3.matcher(date).matches()) {
            try {
                return new SimpleDateFormat("yyyyMMdd").parse(date);
            } catch (Exception ignored) {
            }
        } else if (DATE_PATTERN_4.matcher(date).matches()) {
            try {
                return new SimpleDateFormat("dd/MM/yyyy").parse(date);
            } catch (Exception ignored) {
            }
        } else if (date.charAt(6) == ',') {
            try {
                return new SimpleDateFormat("MMM dd, yyyy").parse(date);
            } catch (Exception ignored) {
            }
        }

        LOGGER.log(Level.SEVERE, "Couldn't parse date: " + date);

        return null;
    }

    private void decrementGameSetPosition(final int gameSetId, final int positionId, final boolean senteWin,
                                          final boolean goteWin) {
        String statement = senteWin ? DECREMENT_GAMESET_POSITION_SENTE_WIN : (goteWin ?
                DECREMENT_GAMESET_POSITION_GOTE_WIN : DECREMENT_GAMESET_POSITION_OTHER);

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setInt(1, positionId);
            preparedStatement.setInt(2, gameSetId);
            int updateResult = preparedStatement.executeUpdate();

            if (updateResult != 1 && updateResult != 2) {
                LOGGER.log(Level.SEVERE, "Could not decrement gameset position");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error decrementing the gameset position in db", e);
        }
    }

    private void incrementGameSetPosition(final int gameSetId, final int positionId, final boolean senteWin,
                                          final boolean goteWin) {
        String statement = senteWin ? INCREMENT_GAMESET_POSITION_SENTE_WIN : (goteWin ?
                INCREMENT_GAMESET_POSITION_GOTE_WIN : INCREMENT_GAMESET_POSITION_OTHER);

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setInt(1, positionId);
            preparedStatement.setInt(2, gameSetId);
            int updateResult = preparedStatement.executeUpdate();

            if (updateResult != 1 && updateResult != 2) {
                LOGGER.log(Level.SEVERE, "Could not increment gameset position");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error incrementing the gameset position in db", e);
        }
    }

    private void incrementGameSetMove(final int gameSetId, final int positionId, final String moveUsf,
                                      final int newPositionId) {

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INCREMENT_GAMESET_MOVE)) {
            preparedStatement.setInt(1, positionId);
            preparedStatement.setString(2, moveUsf);
            preparedStatement.setInt(3, newPositionId);
            preparedStatement.setInt(4, gameSetId);
            int updateResult = preparedStatement.executeUpdate();

            if (updateResult != 1 && updateResult != 2) {
                LOGGER.log(Level.SEVERE, "Could not increment gameset move");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error incrementing the gameset move in db", e);
        }

    }

    private void decrementGameSetMove(final int gameSetId, final int positionId, final String moveUsf,
                                      final int newPositionId) {

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DECREMENT_GAMESET_MOVE)) {
            preparedStatement.setInt(1, positionId);
            preparedStatement.setString(2, moveUsf);
            preparedStatement.setInt(3, newPositionId);
            preparedStatement.setInt(4, gameSetId);
            int updateResult = preparedStatement.executeUpdate();

            if (updateResult != 1 && updateResult != 2) {
                LOGGER.log(Level.SEVERE, "Could not decrement gameset move");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error decrementing the gameset move in db", e);
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
                int moveOccurences = rs.getInt("ps_gamesetmove.num_total");
                String move = rs.getString("move");
                int newPositionOccurences = rs.getInt("ps_gamesetpos.num_total");
                int senteWins = rs.getInt("num_sente_win");
                int goteWins = rs.getInt("num_gote_win");
                int newPositionId = rs.getInt("new_position_id");

//                LOGGER.log(Level.INFO, "Found position move: " + moveOccurences);

                if (moveOccurences > 0) {
                    result.add(new PersistentGameSetMove(move, moveOccurences, positionId, newPositionId, gameSetId,
                            newPositionOccurences, senteWins, goteWins));
                }
            }

            return result;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the gameset in db", e);
            return null;
        }
    }

    public int getGamesCountFromGameSet(final int gameSetId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COUNT_GAMES_FROM_GAMESET)) {
            preparedStatement.setInt(1, gameSetId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("num_games");
            }
            return -1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the gameset games in db", e);
            return -1;
        }
    }

}
