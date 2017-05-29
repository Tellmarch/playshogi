package com.playshogi.library.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.files.GameRecordFileReaderTest;
import com.playshogi.library.shogi.models.GameRecordUtils;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class PositionRepository {

	private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

	private static final String INSERT_POSITION = "INSERT INTO `playshogi`.`ps_position` (`code`)" + " VALUES (?);";
	private static final String SELECT_POSITION_BY_ID = "SELECT * FROM `playshogi`.`ps_position` WHERE id = ?";
	private static final String SELECT_POSITION_BY_CODE = "SELECT * FROM `playshogi`.`ps_position` WHERE code = ?";
	private static final String DELETE_POSITION = "DELETE FROM `playshogi`.`ps_position` WHERE id = ?";

	private final DbConnection dbConnection;

	public PositionRepository(final DbConnection dbConnection) {
		this.dbConnection = dbConnection;
	}

	public int getOrSavePosition(final ShogiPosition shogiPosition) {
		return getOrSavePosition(SfenConverter.toSFEN(shogiPosition));
	}

	public int getOrSavePosition(final String sfen) {
		int positionId = getPositionIdBySfen(sfen);
		if (positionId != -1) {
			return positionId;
		} else {
			return savePosition(sfen);
		}
	}

	public int savePosition(final ShogiPosition shogiPosition) {
		return savePosition(SfenConverter.toSFEN(shogiPosition));
	}

	public int savePosition(final String sfen) {

		int key = -1;

		Connection connection = dbConnection.getConnection();
		try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_POSITION, Statement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setString(1, sfen);
			preparedStatement.executeUpdate();

			ResultSet rs = preparedStatement.getGeneratedKeys();

			if (rs.next()) {
				key = rs.getInt(1);
				LOGGER.log(Level.INFO, "Inserted position with index " + key);
			} else {
				LOGGER.log(Level.SEVERE, "Could not insert position");
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error saving the position in db", e);
		}

		return key;
	}

	public String getPositionSfenById(final int positionId) {
		Connection connection = dbConnection.getConnection();
		try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POSITION_BY_ID)) {
			preparedStatement.setInt(1, positionId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				LOGGER.log(Level.INFO, "Found position: " + rs.getString("code") + " with id: " + rs.getInt("id"));
				String sfen = rs.getString("code");

				return sfen;
			} else {
				LOGGER.log(Level.INFO, "Did not find position: " + positionId);
				return null;
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error looking up the position in db", e);
			return null;
		}
	}

	public int getPositionIdBySfen(final String sfen) {
		Connection connection = dbConnection.getConnection();
		try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_POSITION_BY_CODE)) {
			preparedStatement.setString(1, sfen);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				LOGGER.log(Level.INFO, "Found position: " + rs.getString("code") + " with id: " + rs.getInt("id"));

				return rs.getInt("id");
			} else {
				LOGGER.log(Level.INFO, "Did not find position: " + sfen);
				return -1;
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error looking up the position in db", e);
			return -1;
		}
	}

	public void deletePositionById(final int positionId) {
		Connection connection = dbConnection.getConnection();
		try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_POSITION)) {
			preparedStatement.setInt(1, positionId);
			int rs = preparedStatement.executeUpdate();
			if (rs == 1) {
				LOGGER.log(Level.INFO, "Deleted position: " + positionId);
			} else {
				LOGGER.log(Level.INFO, "Did not find position: " + positionId);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error looking up the position in db", e);
		}
	}

	public static void main(final String[] args) throws IOException {
		GameRecord gameRecord = GameRecordFileReaderTest.getExampleTsumeGameRecord();
		PositionRepository rep = new PositionRepository(new DbConnection());

		Iterable<ShogiPosition> mainVariation = GameRecordUtils.getMainVariation(gameRecord);

		for (ShogiPosition shogiPosition : mainVariation) {
			System.out.println(rep.getOrSavePosition(shogiPosition));
		}

	}

}
