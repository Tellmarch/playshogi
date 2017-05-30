package com.playshogi.library.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playshogi.library.database.models.PersistentGame;

public class GameRepository {

	private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

	private static final String INSERT_GAME = "INSERT INTO `playshogi`.`ps_game` "
			+ "(`kifu_id`, `sente_id`, `gote_id`, `sente_name`, `gote_name`, `date_played`, `venue`, `description`)" + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?);";
	private static final String SELECT_GAME = "SELECT * FROM ps_gamme WHERE id = ?";
	private static final String DELETE_GAME = "DELETE FROM ps_game WHERE id = ?";

	private final DbConnection dbConnection;

	public GameRepository(final DbConnection dbConnection) {
		this.dbConnection = dbConnection;
	}

	public int saveGame(final int kifuId, final Integer senteId, final Integer goteId, final String senteName, final String goteName, final Date datePlayed,
			final int venueId, final String description) {

		int key = -1;

		Connection connection = dbConnection.getConnection();
		try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GAME, Statement.RETURN_GENERATED_KEYS)) {
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

	private void setIntParameterOrNull(final Integer value, final PreparedStatement preparedStatement, final int parameterIndex) throws SQLException {
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

				return new PersistentGame(gameId, kifuId, senteId, goteId, senteName, goteName, datePlayed, venueId, description);
			} else {
				LOGGER.log(Level.INFO, "Did not find game: " + gameId);
				return null;
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error looking up the game in db", e);
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
