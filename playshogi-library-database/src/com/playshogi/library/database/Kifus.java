package com.playshogi.library.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.files.GameRecordFileReaderTest;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;

public class Kifus {

	private static final Logger LOGGER = Logger.getLogger(Users.class.getName());

	private static final String INSERT_KIFU = "INSERT INTO `playshogi`.`ps_kifu` (`name`, `author_id`, `usf`, `type_id`)" + " VALUES (?, ?, ?,  ?);";
	private static final String SELECT_KIFU = "SELECT * FROM ps_kifu WHERE id = ?";
	private static final String DELETE_KIFU = "DELETE FROM ps_kifu WHERE id = ?";

	private final DbConnection dbConnection;

	public Kifus(final DbConnection dbConnection) {
		this.dbConnection = dbConnection;
	}

	public int saveKifu(final GameRecord gameRecord, final String name, final int authorId, final int type) {

		int key = -1;

		Connection connection = dbConnection.getConnection();
		try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_KIFU, Statement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setString(1, name);
			preparedStatement.setInt(2, authorId);
			preparedStatement.setString(3, UsfFormat.INSTANCE.write(gameRecord));
			preparedStatement.setInt(4, type);
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

	public GameRecord getKifuById(final int kifuId) {
		Connection connection = dbConnection.getConnection();
		try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_KIFU)) {
			preparedStatement.setInt(1, kifuId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				LOGGER.log(Level.INFO, "Found kifu: " + rs.getString("name") + " with id: " + rs.getInt("id"));
				String name = rs.getString("name");
				int authorId = rs.getInt("author_id");
				String usf = rs.getString("usf");
				int type = rs.getInt("type_id");
				return null;
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

	public static void main(final String[] args) throws IOException {
		GameRecord gameRecord = GameRecordFileReaderTest.getExampleTsumeGameRecord();
		Kifus kifus = new Kifus(new DbConnection());
		int kifuId = kifus.saveKifu(gameRecord, "test", 1, 2);
		GameRecord kifuById = kifus.getKifuById(kifuId);
		System.out.println(Objects.equals(gameRecord, kifuById));
		kifus.deleteKifuById(kifuId);
	}

}
