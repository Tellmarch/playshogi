package com.playshogi.library.database;

import com.playshogi.library.database.models.PersistentProblem;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProblemRepository {

    private static final Logger LOGGER = Logger.getLogger(ProblemRepository.class.getName());

    private static final String INSERT_PROBLEM = "INSERT INTO `playshogi`.`ps_problem` "
            + "(`kifu_id`, `num_moves`, `elo`, `pb_type`)" + " VALUES ( ?, ?, ?, ?);";
    private static final String SELECT_PROBLEM = "SELECT * FROM ps_problem WHERE id = ?";
    private static final String DELETE_PROBLEM = "DELETE FROM ps_problem WHERE id = ?";

    private final DbConnection dbConnection;

    public ProblemRepository(final DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public int saveProblem(final int kifuId, final Integer numMoves, final Integer elo,
                           final PersistentProblem.ProblemType pbType) {

        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PROBLEM,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, kifuId);
            setIntParameterOrNull(numMoves, preparedStatement, 2);
            setIntParameterOrNull(elo, preparedStatement, 3);
            preparedStatement.setInt(4, pbType.getDbInt());
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                key = rs.getInt(1);
                LOGGER.log(Level.INFO, "Inserted problem with index " + key);
            } else {
                LOGGER.log(Level.SEVERE, "Could not insert problem");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the problem in db", e);
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

    public PersistentProblem getProblemById(final int problemId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PROBLEM)) {
            preparedStatement.setInt(1, problemId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                LOGGER.log(Level.INFO, "Found problem with id: " + rs.getInt("id"));

                int kifuId = rs.getInt("kifu_id");
                int numMoves = rs.getInt("num_moves");
                int elo = rs.getInt("elo");
                int pbType = rs.getInt("pb_type");

                return new PersistentProblem(problemId, kifuId, numMoves, elo,
                        PersistentProblem.ProblemType.fromDbInt(pbType));
            } else {
                LOGGER.log(Level.INFO, "Did not find problem: " + problemId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the problem in db", e);
            return null;
        }
    }

    public void deleteProblemById(final int problemId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PROBLEM)) {
            preparedStatement.setInt(1, problemId);
            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "Deleted problem: " + problemId);
            } else {
                LOGGER.log(Level.INFO, "Did not find problem: " + problemId);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the problem in db", e);
        }
    }

}
