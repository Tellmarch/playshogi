package com.playshogi.library.database;

import com.playshogi.library.database.models.*;
import com.playshogi.library.shogi.models.features.FeatureTag;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.moves.SpecialMove;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final String SELECT_ALL_PROBLEMSETS = "SELECT * FROM `playshogi`.`ps_problemset` LIMIT 1000";
    private static final String SELECT_PUBLIC_PROBLEMSETS = "SELECT * FROM `playshogi`.`ps_problemset` WHERE " +
            "visibility = 2" +
            " LIMIT 1000";
    private static final String SELECT_PROBLEMSETS_FOR_USER = "SELECT * FROM `playshogi`.`ps_problemset` WHERE " +
            "owner_user_id = ? LIMIT 1000";
    private static final String SELECT_PROBLEMSET = "SELECT * FROM `playshogi`.`ps_problemset` WHERE id = ?";
    private static final String SELECT_PROBLEMS_FROM_PROBLEMSET =
            "SELECT * FROM playshogi.ps_problemsetpbs join playshogi.ps_problem" +
                    " on ps_problem.id = problem_id WHERE problemset_id = ? ORDER BY `index` ASC, `problem_id` ASC;";
    private static final String SELECT_VISIBLE_PROBLEMS_FROM_PROBLEMSET = "SELECT * FROM playshogi.ps_problemsetpbs " +
            "join playshogi.ps_problem on ps_problem.id = problem_id " +
            "WHERE problemset_id = ? AND hidden=0 ORDER BY `index` ASC, `problem_id` ASC;";
    private static final String SELECT_COUNT_PROBLEMS_FROM_PROBLEMSET = "SELECT COUNT(*) as num_problems" +
            " FROM playshogi.ps_problemsetpbs WHERE problemset_id = ?;";
    private static final String DELETE_PROBLEMSET = "DELETE FROM `playshogi`.`ps_problemset` WHERE id = ? AND " +
            "owner_user_id" +
            " = ?";
    private static final String UPDATE_PROBLEMSET = "UPDATE `playshogi`.`ps_problemset` " +
            "SET `name` = ?, `description` = ?, `visibility` = ?, `difficulty` = ?, `tags` = ? WHERE `id` = ? AND " +
            "`owner_user_id` = ?;";
    private static final String ADMIN_UPDATE_PROBLEMSET = "UPDATE `playshogi`.`ps_problemset` " +
            "SET `name` = ?, `description` = ?, `visibility` = ?, `difficulty` = ?, `tags` = ? WHERE `id` = ?;";

    private static final String DELETE_PROBLEMSET_PROBLEM = "DELETE ps_problemsetpbs FROM playshogi.ps_problemsetpbs " +
            "JOIN " +
            "playshogi.ps_problemset on id=problemset_id WHERE problemset_id = ? and problem_id=? and owner_user_id " +
            "=?;";

    private static final String COMPUTE_PROBLEM_DIFFICULTY = "select problem_id, kifu_id, success_rate, attempts, " +
            "NTILE(5) over w as difficulty\n" +
            "FROM (select problem_id, kifu_id, num_moves, pb_type, sum(correct)/count(*) as success_rate, count(*) as" +
            " attempts from ps_userpbstats JOIN ps_problem ON (problem_id = id) group by 1,2,3, 4) as t \n" +
            "WHERE num_moves = ? and attempts > 5 and pb_type = 1 WINDOW w AS (ORDER BY success_rate DESC, attempts " +
            "DESC);";

    private static final String UPDATE_INDEX = "UPDATE playshogi.ps_problemsetpbs SET `index` = ? WHERE problemset_id" +
            " = ? AND problem_id = ?;";

    private static final String SWAP_INDEX =
            "UPDATE\n" +
                    "  playshogi.ps_problemsetpbs t1 INNER JOIN playshogi.ps_problemsetpbs t2\n" +
                    "  ON (t1.problemset_id, t2.problemset_id, t1.problem_id, t2.problem_id) IN ((?,?,?,?),(?,?," +
                    "?,?))\n" +
                    "SET\n" +
                    "  t1.index = t2.index;";


    private final ProblemRepository problemRepository;

    public ProblemSetRepository(final DbConnection dbConnection) {
        this.dbConnection = dbConnection;
        problemRepository = new ProblemRepository(dbConnection);
    }

    public int saveProblemSet(final String name, final String description, final Visibility visibility
            , final Integer ownerId, final Integer difficulty, final String[] tags) {

        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PROBLEMSET,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, visibility.ordinal());
            setInteger(preparedStatement, 4, ownerId);
            setInteger(preparedStatement, 5, difficulty);
            preparedStatement.setString(6, tags == null ? "" : String.join(",", tags));
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

        Integer problemId = addKifuToProblemSet(problemSetId, elo, pbType, kifuId, numMoves);

        if (problemId == null) return;

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

    public void addKifuToProblemSet(final int problemSetId, final int elo,
                                    final PersistentProblem.ProblemType pbType,
                                    final int kifuId) {
        KifuRepository kifuRepository = new KifuRepository(dbConnection);
        PersistentKifu kifuById = kifuRepository.getKifuById(kifuId);

        if (kifuById == null) {
            return;
        }

        int numMoves = kifuById.getKifu().getGameTree().getMainVariationLength();


        addKifuToProblemSet(problemSetId, elo, pbType, kifuId, numMoves);
    }

    private Integer addKifuToProblemSet(final int problemSetId, final int elo,
                                        final PersistentProblem.ProblemType pbType,
                                        final int kifuId, final int numMoves) {
        ProblemRepository problemRepository = new ProblemRepository(dbConnection);

        int problemId = problemRepository.saveProblem(kifuId, numMoves, elo, pbType);

        if (problemId == -1) {
            return null;
        }

        addProblemSetProblemRecord(problemSetId, problemId);
        return problemId;
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

    public List<PersistentProblemSet> getAllProblemSets() {
        List<PersistentProblemSet> result = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PROBLEMSETS)) {
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
            LOGGER.log(Level.SEVERE, "Error retrieving problem sets in db", e);
        }
        return result;
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

    public List<PersistentProblemInCollection> getProblemsFromProblemSet(final int problemSetId,
                                                                         final boolean includeHiddenProblems) {
        ArrayList<PersistentProblemInCollection> problems = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(includeHiddenProblems ?
                SELECT_PROBLEMS_FROM_PROBLEMSET : SELECT_VISIBLE_PROBLEMS_FROM_PROBLEMSET)) {
            preparedStatement.setInt(1, problemSetId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int kifuId = rs.getInt("kifu_id");
                int numMoves = rs.getInt("num_moves");
                int elo = rs.getInt("elo");
                int pbType = rs.getInt("pb_type");
                int index = rs.getInt("index");
                boolean hidden = rs.getBoolean("hidden");

                PersistentProblem persistentProblem = new PersistentProblem(id, kifuId, numMoves, elo,
                        PersistentProblem.ProblemType.fromDbInt(pbType));
                problems.add(new PersistentProblemInCollection(persistentProblem, problemSetId, index, hidden));
            }
            return problems;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the problemset problems in db", e);
            return null;
        }
    }

    public int getProblemsCountFromProblemSet(final int problemSetId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_COUNT_PROBLEMS_FROM_PROBLEMSET)) {
            preparedStatement.setInt(1, problemSetId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("num_problems");
            }
            return -1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error looking up the problemset problems in db", e);
            return -1;
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

    public void updateProblemSet(final int id, final String name, final String description,
                                 final Visibility visibility, final Integer difficulty, final String[] tags,
                                 final Integer ownerId) {
        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PROBLEMSET)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, visibility.ordinal());
            setInteger(preparedStatement, 4, difficulty);
            preparedStatement.setString(5, tags == null ? "" : String.join(",", tags));
            preparedStatement.setInt(6, id);
            setInteger(preparedStatement, 7, ownerId);
            int res = preparedStatement.executeUpdate();

            if (res == 1) {
                LOGGER.log(Level.INFO, "Updated problemset with index " + key);
            } else {
                LOGGER.log(Level.SEVERE, "Could not update problemset (res = " + res + ")");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating the problemset in db", e);
        }
    }

    public void adminUpdateProblemSet(final int id, final String name, final String description,
                                      final Visibility visibility, final Integer difficulty, final String[] tags) {
        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(ADMIN_UPDATE_PROBLEMSET)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, visibility.ordinal());
            setInteger(preparedStatement, 4, difficulty);
            preparedStatement.setString(5, tags == null ? "" : String.join(",", tags));
            preparedStatement.setInt(6, id);
            int res = preparedStatement.executeUpdate();

            if (res == 1) {
                LOGGER.log(Level.INFO, "Updated problemset with index " + key);
            } else {
                LOGGER.log(Level.SEVERE, "Could not update problemset (res = " + res + ")");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating the problemset in db", e);
        }
    }


    public boolean deleteProblemFromProblemSet(final int problemId, final int problemSetId, final int userId) {
        boolean result = deleteFromProblemSetTable(problemId, problemSetId, userId);

        if (!result) { // No permission
            return false;
        }

        problemRepository.deleteProblemById(problemId);
        return true;
    }

    private boolean deleteFromProblemSetTable(final int problemId, final int problemSetId, final int userId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PROBLEMSET_PROBLEM)) {
            preparedStatement.setInt(1, problemSetId);
            preparedStatement.setInt(2, problemId);
            preparedStatement.setInt(3, userId);
            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "Deleted problem from problemset: " + problemId + " " + problemSetId);
                return true;
            } else {
                LOGGER.log(Level.INFO, "Could not delete problem: " + problemId + " " + problemSetId);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting up the problem from problemset in db", e);
            return false;
        }
    }

    public void createCollectionsByDifficulty(final int userId, int numMoves) {
        Map<Integer, List<Integer>> byDifficulty = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            byDifficulty.put(i, new ArrayList<>());
        }
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(COMPUTE_PROBLEM_DIFFICULTY)) {
            preparedStatement.setInt(1, numMoves);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int kifuId = rs.getInt("kifu_id");
                int difficulty = rs.getInt("difficulty");

                byDifficulty.get(difficulty).add(kifuId);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in createCollectionsByDifficulty", e);
        }

        System.out.println(byDifficulty);

        for (int i = 1; i <= 5; i++) {
            int problemSet = saveProblemSet("Tsume in " + numMoves + ": " + i + "/5", "", Visibility.UNLISTED, userId
                    , i,
                    new String[]{"Tsume"});
            List<Integer> kifus = byDifficulty.get(i);
            for (Integer kifuId : kifus) {
                addKifuToProblemSet(problemSet, 0, PersistentProblem.ProblemType.UNSPECIFIED, kifuId, numMoves);
            }
        }
    }

    public void updateIndexesForProblemSet(final int problemSetId) {
        LOGGER.log(Level.INFO, "Updating indexes for problemset: " + problemSetId);
        List<PersistentProblemInCollection> problemsFromProblemSet = getProblemsFromProblemSet(problemSetId, true);
        for (int i = 0; i < problemsFromProblemSet.size(); i++) {
            updateProblemIndex(problemSetId, problemsFromProblemSet, i);
        }
    }

    private void updateProblemIndex(final int problemSetId,
                                    final List<PersistentProblemInCollection> problemsFromProblemSet, final int index) {
        PersistentProblemInCollection problem = problemsFromProblemSet.get(index);
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_INDEX)) {
            preparedStatement.setInt(1, index + 1);
            preparedStatement.setInt(2, problemSetId);
            preparedStatement.setInt(3, problem.getProblem().getId());
            int res = preparedStatement.executeUpdate();

            if (res == 1) {
                LOGGER.log(Level.INFO,
                        "Updated problemset index: " + problemSetId + " - " + problem.getProblem().getId() + " - "
                                + index);
            } else {
                LOGGER.log(Level.SEVERE, "Could not update problemset index (res = " + res + ")");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating the problemset in db", e);
        }
    }

    public void swapProblemsInCollection(final int problemSetId, final int problem1, final int problem2) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SWAP_INDEX)) {
            preparedStatement.setInt(1, problemSetId);
            preparedStatement.setInt(2, problemSetId);
            preparedStatement.setInt(3, problem1);
            preparedStatement.setInt(4, problem2);
            preparedStatement.setInt(5, problemSetId);
            preparedStatement.setInt(6, problemSetId);
            preparedStatement.setInt(7, problem2);
            preparedStatement.setInt(8, problem1);

            int res = preparedStatement.executeUpdate();

            if (res == 2) {
                LOGGER.log(Level.INFO,
                        "Swapped problemset index: " + problemSetId + " - " + problem1 + " - " + problem2);
            } else {
                LOGGER.log(Level.SEVERE, "Could not swap problemset index (res = " + res + ")");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error swapping the problemset in db", e);
        }
    }
}
