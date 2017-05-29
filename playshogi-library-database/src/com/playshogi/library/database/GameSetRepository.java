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

public class GameSetRepository {

	private static final Logger LOGGER = Logger.getLogger(Users.class.getName());

	private static final String INSERT_GAMESET = "INSERT INTO `playshogi`.`ps_gameset` (`name`)" + " VALUES (?);";
	private static final String SELECT_GAMESET = "SELECT * FROM `playshogi`.`ps_gameset` WHERE id = ?";
	private static final String DELETE_GAMESET = "DELETE FROM `playshogi`.`ps_gameset` WHERE id = ?";

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

	public static void main(final String[] args) throws IOException {
		GameSetRepository rep = new GameSetRepository(new DbConnection());

		int id = rep.saveGameSet("Test");
		System.out.println(rep.getGameSetById(id).getName());
		rep.deleteGamesetById(id);

	}

}
