package com.playshogi.library.database;

import com.playshogi.library.database.models.PersistentKifu;
import com.playshogi.library.database.models.PersistentProblem;
import com.playshogi.library.database.models.PersistentProblemSet;
import com.playshogi.library.database.models.Visibility;
import com.playshogi.library.shogi.models.features.FeatureTag;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.moves.SpecialMove;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.playshogi.library.database.SqlUtils.setInteger;

public class ProblemSetRepository {

    private static final Logger LOGGER = Logger.getLogger(ProblemSetRepository.class.getName());

    private final DbConnection dbConnection;

    private static final String INSERT_PROBLEM_TAG = "INSERT INTO `playshogi`.`ps_problemtag` (`problem_id`, " +
            "`tag_id`) VALUES (?, ?);";
    private static final String INSERT_PROBLEMSET = "INSERT INTO `playshogi`.`ps_problemset` (`name`, `description`, " +
            "`visibility`, `owner_user_id`, `difficulty`, `tags`) VALUES (?, ?, ?, ?, ?, ?);";
    private static final String INSERT_PROBLEMSET_PROBLEM = "INSERT INTO `playshogi`.`ps_problemsetpbs`" +
            "(`problemset_id`,`problem_id`) VALUES (?,?);";
    private static final String SELECT_PUBLIC_PROBLEMSETS = "SELECT * FROM `playshogi`.`ps_problemset` WHERE " +
            "visibility = 2" +
            " LIMIT 1000";
    private static final String SELECT_PROBLEMSETS_FOR_USER = "SELECT * FROM `playshogi`.`ps_problemset` WHERE " +
            "owner_user_id = ? LIMIT 1000";
    private static final String SELECT_PROBLEMSET = "SELECT * FROM `playshogi`.`ps_problemset` WHERE id = ?";
    private static final String SELECT_PROBLEMS_FROM_PROBLEMSET = "SELECT * FROM playshogi.ps_problemsetpbs join " +
            "playshogi" +
            ".ps_problem on ps_problem.id = problem_id WHERE problemset_id = ?;";
    private static final String DELETE_PROBLEMSET = "DELETE FROM `playshogi`.`ps_problemset` WHERE id = ? AND " +
            "owner_user_id" +
            " = ?";

    public ProblemSetRepository(final DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public int saveProblemSet(final String name, final String description, final Visibility visibility
            , final Integer ownerId, final Integer difficulty, final String tags) {

        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PROBLEMSET,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, visibility.ordinal());
            setInteger(preparedStatement, 4, ownerId);
            setInteger(preparedStatement, 5, difficulty);
            preparedStatement.setString(6, tags);
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                key = rs.getInt(1);
                LOGGER.log(Level.INFO, "Inserted problemset with index " + key);
            } else {
                LOGGER.log(Level.SEVERE, "Could not insert problemset");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the problemset in db", e);
        }

        return key;
    }

    public void addProblemToProblemSet(final GameRecord gameRecord, final int problemSetId, final String problemName,
                                       final int authorId, final int elo, final PersistentProblem.ProblemType pbType,
                                       final boolean extractFeatureTags) {
        KifuRepository kifuRepository = new KifuRepository(dbConnection);
        ProblemRepository problemRepository = new ProblemRepository(dbConnection);

        int kifuId = kifuRepository.saveKifu(gameRecord, problemName, authorId, PersistentKifu.KifuType.PROBLEM);

        if (kifuId == -1) {
            return;
        }

        GameNavigation gameNavigation = new GameNavigation(new ShogiRulesEngine(), gameRecord.getGameTree());

        kifuRepository.saveKifuPosition(kifuId, gameNavigation.getPosition());

        int numMoves = 0;
        while (gameNavigation.canMoveForward()) {
            gameNavigation.moveForward();
            Move move = gameNavigation.getCurrentMove();
            if (!(move instanceof SpecialMove)) {
                numMoves++;
            }
            kifuRepository.saveKifuPosition(kifuId, gameNavigation.getPosition());
        }

        int problemId = problemRepository.saveProblem(kifuId, numMoves, elo, pbType);

        if (problemId == -1) {
            return;
        }

        addProblemSetProblemRecord(problemSetId, problemId);

        if (extractFeatureTags) {
            for (FeatureTag tag : FeatureTag.values()) {
                try {
                    if (tag.getFeature().hasFeature(gameRecord)) {
                        LOGGER.log(Level.INFO, "Found feature: " + tag.getFeature().getName());
                        insertProblemTag(problemId, tag.getDbIndex());
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error evaluating the feature " + tag.getFeature().getName(), e);
                }
            }
        }
    }

    public void addProblemSetProblemRecord(final int problemSetId, final int problemId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PROBLEMSET_PROBLEM)) {
            preparedStatement.setInt(1, problemSetId);
            preparedStatement.setInt(2, problemId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the problemSetId-problemId in db", e);
        }
    }

    public void insertProblemTag(final int problemId, final int tagId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PROBLEM_TAG)) {
            preparedStatement.setInt(1, problemId);
            preparedStatement.setInt(2, tagId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the problem tag in db", e);
        }
    }

    public List<PersistentProblemSet> getAllPublicProblemSets() {
        List<PersistentProblemSet> result = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PUBLIC_PROBLEMSETS)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                String description = rs.getString("description");
                Visibility visibility = Visibility.values()[rs.getInt("visibility"
                )];
                Integer ownerId = SqlUtils.getInteger(rs, "owner_user_id");
                Integer difficulty = SqlUtils.getInteger(rs, "difficulty");
                String tags = rs.getString("tags");

                result.add(new PersistentProblemSet(id, name, description, visibility, ownerId, difficulty,
                        tags == null ? null : tags.split(",")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving public problem sets in db", e);
        }
        return result;
    }

    public List<PersistentProblemSet> getUserProblemSets(int userId) {
        List<PersistentProblemSet> result = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PROBLEMSETS_FOR_USER)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                String description = rs.getString("description");
                Visibility visibility = Visibility.values()[rs.getInt("visibility"
                )];
                Integer ownerId = SqlUtils.getInteger(rs, "owner_user_id");
                Integer difficulty = SqlUtils.getInteger(rs, "difficulty");
                String tags = rs.getString("tags");

                result.add(new PersistentProblemSet(id, name, description, visibility, ownerId, difficulty,
                        tags == null ? null : tags.split(",")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving problem sets for user in db", e);
        }
        return result;
    }

    public PersistentProblemSet getProblemSetById(final int problemSetId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PROBLEMSET)) {
            preparedStatement.setInt(1, problemSetId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                String description = rs.getString("description");
                Visibility visibility = Visibility.values()[rs.getInt("visibility"
                )];
                Integer ownerId = SqlUtils.getInteger(rs, "owner_user_id");
                Integer difficulty = SqlUtils.getInteger(rs, "difficulty");
                String tags = rs.getString("tags");

                return new PersistentProblemSet(id, name, description, visibility, ownerId, difficulty,
                        tags == null ? null : tags.split(","));
            } else {
                LOGGER.log(Level.INFO, "Problem set not found in DB: " + problemSetId);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving problem set in db", e);
        }
        return null;
    }

    public List<PersistentProblem> getProblemsFromProblemSet(final int problemSetId) {
        ArrayList<PersistentProblem> problems = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PROBLEMS_FROM_PROBLEMSET)) {
            preparedStatement.setInt(1, problemSetId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int kifuId = rs.getInt("kifu_id");
                int numMoves = rs.getInt("num_moves");
                int elo = rs.getInt("elo");
                int pbType = rs.getInt("pb_type");

                problems.add(new PersistentProblem(id, kifuId, numMoves, elo,
                        PersistentProblem.ProblemType.fromDbInt(pbType)));
            }
            return problems;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the problemset problems in db", e);
            return null;
        }
    }

    public boolean deleteProblemsetById(final int problemSetId, final int userId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PROBLEMSET)) {
            preparedStatement.setInt(1, problemSetId);
            preparedStatement.setInt(2, userId);
            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "Deleted problemSet: " + problemSetId);
                return true;
            } else {
                LOGGER.log(Level.INFO, "Did not find problemSet: " + problemSetId);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting up the problemSet in db", e);
            return false;
        }
    }
}
