package com.playshogi.library.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playshogi.library.database.models.PersistentGameSet;
import com.playshogi.library.database.models.PersistentGameSetPos;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.models.record.GameResult;
import com.playshogi.library.shogi.models.GameRecordUtils;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class GameSetRepository {

	private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

	private static final String INSERT_GAMESET = "INSERT INTO `playshogi`.`ps_gameset` (`name`)" + " VALUES (?);";
	private static final String SELECT_GAMESET = "SELECT * FROM `playshogi`.`ps_gameset` WHERE id = ?";
	private static final String DELETE_GAMESET = "DELETE FROM `playshogi`.`ps_gameset` WHERE id = ?";

	private static final String INCREMENT_GAMESET_POSITION_SENTE_WIN = "INSERT INTO `playshogi`.`ps_gamesetpos` (`position_id`, `gameset_id`, `num_total`, `num_sente_win`, `num_gote_win`)"
			+ " VALUES (?, ?, 1, 1, 0) ON DUPLICATE KEY UPDATE num_sente_win=num_sente_win+1,num_total=num_total+1;";

	private static final String INCREMENT_GAMESET_POSITION_GOTE_WIN = "INSERT INTO `playshogi`.`ps_gamesetpos` (`position_id`, `gameset_id`, `num_total`, `num_sente_win`, `num_gote_win`)"
			+ " VALUES (?, ?, 1, 0, 1) ON DUPLICATE KEY UPDATE num_gote_win=num_gote_win+1,num_total=num_total+1;";

	private static final String INCREMENT_GAMESET_POSITION_OTHER = "INSERT INTO `playshogi`.`ps_gamesetpos` (`position_id`, `gameset_id`, `num_total`, `num_sente_win`, `num_gote_win`)"
			+ " VALUES (?, ?, 1, 0, 0) ON DUPLICATE KEY UPDATE num_total=num_total+1;";

	private static final String SELECT_GAMESET_POSITION = "SELECT * FROM `playshogi`.`ps_gamesetpos` WHERE position_id = ? AND gameset_id = ?";

	private final DbConnection dbConnection;

	public GameSetRepository(final DbConnection dbConnection) {
		this.dbConnection = dbConnection;
	}

	public int saveGameSet(final String name) {

		int key = -1;

		Connection connection = dbConnection.getConnection();
		try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GAMESET, Statement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setString(1, name);
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

	public PersistentGameSet getGameSetById(final int gameSetId) {
		Connection connection = dbConnection.getConnection();
		try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GAMESET)) {
			preparedStatement.setInt(1, gameSetId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				LOGGER.log(Level.INFO, "Found gameset: " + rs.getString("name") + " with id: " + rs.getInt("id"));
				String name = rs.getString("name");

				return new PersistentGameSet(gameSetId, name);
			} else {
				LOGGER.log(Level.INFO, "Did not find gameset: " + gameSetId);
				return null;
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error looking up the gameset in db", e);
			return null;
		}
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

	public void addGameToGameSet(final GameRecord gameRecord, final int gameSetId) {
		PositionRepository rep = new PositionRepository(dbConnection);

		boolean senteWin = gameRecord.getGameResult() == GameResult.SENTE_WIN;
		boolean goteWin = gameRecord.getGameResult() == GameResult.GOTE_WIN;

		Iterable<ShogiPosition> mainVariation = GameRecordUtils.getMainVariation(gameRecord);

		for (ShogiPosition shogiPosition : mainVariation) {
			int positionId = rep.getOrSavePosition(shogiPosition);

			incrementGameSetPosition(gameSetId, positionId, senteWin, goteWin);
		}

	}

	public void incrementGameSetPosition(final int gameSetId, final int positionId, final boolean senteWin, final boolean goteWin) {

		String statement = senteWin ? INCREMENT_GAMESET_POSITION_SENTE_WIN : (goteWin ? INCREMENT_GAMESET_POSITION_GOTE_WIN : INCREMENT_GAMESET_POSITION_OTHER);

		Connection connection = dbConnection.getConnection();
		try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
			preparedStatement.setInt(1, positionId);
			preparedStatement.setInt(2, gameSetId);
			int updateResult = preparedStatement.executeUpdate();

			if (updateResult == 1) {
				LOGGER.log(Level.INFO, "Inserted gameset position");
			} else if (updateResult == 2) {
				LOGGER.log(Level.INFO, "Incremented gameset position");
			} else {
				LOGGER.log(Level.SEVERE, "Could not insert gameset position");
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error inserting the gameset position in db", e);
		}

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

				PersistentGameSetPos persistentGameSetPos = new PersistentGameSetPos(positionId, gameSetId, total, senteWins, goteWins);

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

	public static void main(final String[] args) throws IOException {
		GameSetRepository rep = new GameSetRepository(new DbConnection());

		int id = rep.saveGameSet("Test");
		System.out.println(rep.getGameSetById(id).getName());
		rep.deleteGamesetById(id);

	}

}
