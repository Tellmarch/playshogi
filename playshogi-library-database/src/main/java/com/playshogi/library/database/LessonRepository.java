package com.playshogi.library.database;

import com.playshogi.library.database.models.*;

import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.playshogi.library.database.SqlUtils.setInteger;

public class LessonRepository {
    private static final Logger LOGGER = Logger.getLogger(LessonRepository.class.getName());

    private static final String SELECT_VISIBLE_LESSONS = "SELECT * FROM `playshogi`.`ps_lessons` WHERE hidden = 0 and" +
            " old_campaign = 1;";
    private static final String SELECT_VISIBLE_LESSONS_WITH_USER_PROGRESS =
            "SELECT * FROM `playshogi`.`ps_lessons` " +
                    "LEFT JOIN (SELECT * FROM `playshogi`.`ps_userlessonsprogress` WHERE user_id = ?) p " +
                    "ON (id = lesson_id) WHERE hidden = 0 and old_campaign = 1;";
    private static final String SELECT_ALL_LESSONS = "SELECT * FROM `playshogi`.`ps_lessons`;";
    private static final String SELECT_LESSON = "SELECT * FROM `playshogi`.`ps_lessons` WHERE id = ?;";
    private static final String INSERT_LESSON = "INSERT INTO `playshogi`.`ps_lessons` (`kifu_id`, `parent_id`, " +
            "`title`, `description`, `tags`, `preview_sfen`, `difficulty`, `author_id`, `hidden`, " +
            "`type`, `index`, `problemset_id`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_LESSON = "UPDATE `playshogi`.`ps_lessons` SET `kifu_id` = ?, `parent_id` = ?, " +
            "`title` = ?, `description` = ?, `tags` = ?, `preview_sfen` = ?, `difficulty` = ?, `author_id` = ?, " +
            "`hidden` = ?, " +
            "`type` = ?, `index` = ?, `problemset_id` = ? WHERE `id` = ?;";

    private static final String INSERT_CAMPAIGN_LESSON =
            "INSERT INTO `playshogi`.`ps_campaign_lesson` " +
                    "(`campaign_id`, `lesson_id`, `x`, `y`, `optional`, `extra`, `boss`, `important`) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_CAMPAIGN_LESSON =
            "UPDATE `playshogi`.`ps_campaign_lesson` " +
                    "SET `x` = ?, `y` = ?, `optional` = ?, `extra` = ?, `boss` = ?, `important` = ? " +
                    "WHERE `campaign_id` = ? AND `lesson_id` = ?;";

    private static final String DELETE_PREREQUISITES =
            "DELETE FROM `playshogi`.`ps_campaign_lesson_prerequisite` " +
                    "WHERE `campaign_id` = ? AND `lesson_id` = ?;";

    private static final String INSERT_PREREQUISITE =
            "INSERT INTO `playshogi`.`ps_campaign_lesson_prerequisite` " +
                    "(`campaign_id`, `lesson_id`, `prerequisite_lesson_id`) VALUES (?, ?, ?);";

    private static final String SELECT_CAMPAIGN_LESSONS =
            "SELECT * FROM `playshogi`.`ps_campaign_lesson` WHERE `campaign_id` = ?;";

    private static final String SELECT_PREREQUISITES =
            "SELECT `prerequisite_lesson_id` FROM `playshogi`.`ps_campaign_lesson_prerequisite` " +
                    "WHERE `campaign_id` = ? AND `lesson_id` = ?;";

    private static final String SELECT_CAMPAIGN_LESSONS_WITH_PROGRESS =
            "SELECT cl.lesson_id, cl.x, cl.y, cl.optional, cl.extra, cl.boss, cl.important, cl.draft, " +
                    "       l.title, l.difficulty, " +
                    "       IFNULL(ulp.complete, 0) AS complete, IFNULL(ulp.percentage, 0) AS percentage " +
                    "FROM `playshogi`.`ps_campaign_lesson` cl " +
                    "JOIN `playshogi`.`ps_lessons` l ON cl.lesson_id = l.id " +
                    "LEFT JOIN `playshogi`.`ps_userlessonsprogress` ulp " +
                    "       ON cl.lesson_id = ulp.lesson_id AND ulp.user_id = ? " +
                    "WHERE cl.campaign_id = ?;";

    private static final String SELECT_CAMPAIGN_INFO =
            "SELECT id, title, description FROM `playshogi`.`ps_campaign` WHERE id = ?;";

    private static final String SELECT_ALL_PREREQUISITES_FOR_CAMPAIGN =
            "SELECT lesson_id, prerequisite_lesson_id " +
                    "FROM `playshogi`.`ps_campaign_lesson_prerequisite` " +
                    "WHERE campaign_id = ?;";


    private static final String CHECK_CAMPAIGN_AUTHOR =
            "SELECT 1 FROM `playshogi`.`ps_campaign` WHERE id = ? AND author_id = ?";

    private final DbConnection dbConnection;

    public LessonRepository(final DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public PersistentLesson getLesson(final int lessonId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_LESSON)) {
            preparedStatement.setInt(1, lessonId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                Integer kifuId = SqlUtils.getInteger(rs, "kifu_id");
                Integer problemCollectionId = SqlUtils.getInteger(rs, "problemset_id");
                Integer parentId = SqlUtils.getInteger(rs, "parent_id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String tags = rs.getString("tags");
                String previewSfen = rs.getString("preview_sfen");
                Integer difficulty = SqlUtils.getInteger(rs, "difficulty");
                int likes = rs.getInt("likes");
                Integer authorId = SqlUtils.getInteger(rs, "author_id");
                boolean hidden = rs.getBoolean("hidden");
                Date creationDate = rs.getDate("create_time");
                Date updateDate = rs.getDate("update_time");
                PersistentLesson.LessonType type = PersistentLesson.LessonType.fromDbInt(rs.getInt("type"));
                int index = rs.getInt("index");

                return new PersistentLesson(id, kifuId, problemCollectionId, parentId, title, description,
                        tags == null ? null : tags.split(","), previewSfen,
                        difficulty, likes, authorId, hidden, creationDate, updateDate, type, index);
            } else {
                LOGGER.log(Level.INFO, "Did not find lesson: " + lessonId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving visible lessons in db", e);
        }
        return null;
    }

    public List<PersistentLesson> getAllVisibleLessons() {
        List<PersistentLesson> result = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_VISIBLE_LESSONS)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                Integer kifuId = SqlUtils.getInteger(rs, "kifu_id");
                Integer problemCollectionId = SqlUtils.getInteger(rs, "problemset_id");
                Integer parentId = SqlUtils.getInteger(rs, "parent_id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String tags = rs.getString("tags");
                String previewSfen = rs.getString("preview_sfen");
                Integer difficulty = SqlUtils.getInteger(rs, "difficulty");
                int likes = rs.getInt("likes");
                Integer authorId = SqlUtils.getInteger(rs, "author_id");
                boolean hidden = rs.getBoolean("hidden");
                Date creationDate = rs.getDate("create_time");
                Date updateDate = rs.getDate("update_time");
                PersistentLesson.LessonType type = PersistentLesson.LessonType.fromDbInt(rs.getInt("type"));
                int index = rs.getInt("index");

                result.add(new PersistentLesson(id, kifuId, problemCollectionId, parentId, title, description,
                        tags == null ? null : tags.split(","), previewSfen,
                        difficulty, likes, authorId, hidden, creationDate, updateDate, type, index));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving visible lessons in db", e);
        }
        return result;
    }

    public List<PersistentLessonWithUserProgress> getAllVisibleLessonsWithUserProgress(final int userId) {
        List<PersistentLessonWithUserProgress> result = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(SELECT_VISIBLE_LESSONS_WITH_USER_PROGRESS)) {
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                Integer kifuId = SqlUtils.getInteger(rs, "kifu_id");
                Integer problemCollectionId = SqlUtils.getInteger(rs, "problemset_id");
                Integer parentId = SqlUtils.getInteger(rs, "parent_id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String tags = rs.getString("tags");
                String previewSfen = rs.getString("preview_sfen");
                Integer difficulty = SqlUtils.getInteger(rs, "difficulty");
                int likes = rs.getInt("likes");
                Integer authorId = SqlUtils.getInteger(rs, "author_id");
                boolean hidden = rs.getBoolean("hidden");
                Date creationDate = rs.getDate("create_time");
                Date updateDate = rs.getDate("update_time");
                PersistentLesson.LessonType type = PersistentLesson.LessonType.fromDbInt(rs.getInt("type"));
                int index = rs.getInt("index");

                PersistentLesson lesson = new PersistentLesson(id, kifuId, problemCollectionId, parentId, title,
                        description,
                        tags == null ? null : tags.split(","), previewSfen,
                        difficulty, likes, authorId, hidden, creationDate, updateDate, type, index);

                Date timeViewed = rs.getDate("time_viewed");
                Integer timeSpentMs = SqlUtils.getInteger(rs, "time_spent_ms");
                boolean complete = rs.getBoolean("complete");
                int percentage = rs.getInt("percentage");
                Integer rating = SqlUtils.getInteger(rs, "rating");

                PersistentUserLessonProgress progress = new PersistentUserLessonProgress(userId, id, timeViewed,
                        timeSpentMs, complete, percentage, rating);

                result.add(new PersistentLessonWithUserProgress(lesson, progress));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving visible lessons in db", e);
        }
        return result;
    }

    public List<PersistentLesson> getAllLessons() {
        List<PersistentLesson> result = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_LESSONS)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                Integer kifuId = SqlUtils.getInteger(rs, "kifu_id");
                Integer problemCollectionId = SqlUtils.getInteger(rs, "problemset_id");
                Integer parentId = SqlUtils.getInteger(rs, "parent_id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String tags = rs.getString("tags");
                String previewSfen = rs.getString("preview_sfen");
                Integer difficulty = SqlUtils.getInteger(rs, "difficulty");
                int likes = rs.getInt("likes");
                Integer authorId = SqlUtils.getInteger(rs, "author_id");
                boolean hidden = rs.getBoolean("hidden");
                Date creationDate = rs.getDate("create_time");
                Date updateDate = rs.getDate("update_time");
                PersistentLesson.LessonType type = PersistentLesson.LessonType.fromDbInt(rs.getInt("type"));
                int index = rs.getInt("index");

                result.add(new PersistentLesson(id, kifuId, problemCollectionId, parentId, title, description,
                        tags == null ? null : tags.split(","), previewSfen,
                        difficulty, likes, authorId, hidden, creationDate, updateDate, type, index));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving visible lessons in db", e);
        }
        return result;
    }

    public int saveLesson(final PersistentLesson lesson) {

        int key = -1;

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LESSON,
                Statement.RETURN_GENERATED_KEYS)) {
            setInteger(preparedStatement, 1, lesson.getKifuId());
            setInteger(preparedStatement, 2, lesson.getParentId());
            preparedStatement.setString(3, lesson.getTitle());
            preparedStatement.setString(4, lesson.getDescription());
            preparedStatement.setString(5, lesson.getTags() == null ? "" : String.join(",", lesson.getTags()));
            preparedStatement.setString(6, lesson.getPreviewSfen());
            setInteger(preparedStatement, 7, lesson.getDifficulty());
            setInteger(preparedStatement, 8, lesson.getAuthorId());
            preparedStatement.setBoolean(9, lesson.isHidden());
            preparedStatement.setInt(10, lesson.getType().getDbInt());
            preparedStatement.setInt(11, lesson.getIndex());
            setInteger(preparedStatement, 12, lesson.getProblemCollectionId());
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                key = rs.getInt(1);
                LOGGER.log(Level.INFO, "Inserted lesson with index " + key);
            } else {
                LOGGER.log(Level.SEVERE, "Could not insert lesson");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving the lesson in db", e);
        }

        return key;
    }

    public void updateLesson(final PersistentLesson lesson) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LESSON,
                Statement.RETURN_GENERATED_KEYS)) {
            setInteger(preparedStatement, 1, lesson.getKifuId());
            setInteger(preparedStatement, 2, lesson.getParentId());
            preparedStatement.setString(3, lesson.getTitle());
            preparedStatement.setString(4, lesson.getDescription());
            preparedStatement.setString(5, lesson.getTags() == null ? "" : String.join(",", lesson.getTags()));
            preparedStatement.setString(6, lesson.getPreviewSfen());
            setInteger(preparedStatement, 7, lesson.getDifficulty());
            setInteger(preparedStatement, 8, lesson.getAuthorId());
            preparedStatement.setBoolean(9, lesson.isHidden());
            preparedStatement.setInt(10, lesson.getType().getDbInt());
            preparedStatement.setInt(11, lesson.getIndex());
            setInteger(preparedStatement, 12, lesson.getProblemCollectionId());
            preparedStatement.setInt(13, lesson.getId());

            int res = preparedStatement.executeUpdate();

            if (res == 1) {
                LOGGER.log(Level.INFO, "Updated lesson");
            } else {
                LOGGER.log(Level.SEVERE, "Could not update lesson: " + preparedStatement);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating the lesson in db", e);
        }
    }

    public boolean addLessonToCampaign(PersistentCampaignLesson cLesson) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement ps =
                     connection.prepareStatement(INSERT_CAMPAIGN_LESSON)) {
            ps.setInt(1, cLesson.getCampaignId());
            ps.setInt(2, cLesson.getLessonId());
            ps.setInt(3, cLesson.getX());
            ps.setInt(4, cLesson.getY());
            ps.setBoolean(5, cLesson.isOptional());
            ps.setBoolean(6, cLesson.isExtra());
            ps.setBoolean(7, cLesson.isBoss());
            ps.setBoolean(8, cLesson.isImportant());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding lesson to campaign", e);
            return false;
        }
    }

    public boolean updateCampaignLesson(PersistentCampaignLesson cLesson) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement ps =
                     connection.prepareStatement(UPDATE_CAMPAIGN_LESSON)) {

            ps.setInt(1, cLesson.getX());
            ps.setInt(2, cLesson.getY());
            ps.setBoolean(3, cLesson.isOptional());
            ps.setBoolean(4, cLesson.isExtra());
            ps.setBoolean(5, cLesson.isBoss());
            ps.setBoolean(6, cLesson.isImportant());
            ps.setInt(7, cLesson.getCampaignId());
            ps.setInt(8, cLesson.getLessonId());

            int rs = ps.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "updated campaign lesson: " + cLesson);
                return true;
            } else {
                LOGGER.log(Level.INFO, "Did not find campaign lesson: " + cLesson);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating campaign lesson", e);
            return false;
        }
    }

    public void updateLessonPrerequisites(int campaignId, int lessonId, List<Integer> prerequisiteIds) {
        Connection connection = dbConnection.getConnection();

        try {
            connection.setAutoCommit(false);

            // 1. Remove existing prerequisites
            try (PreparedStatement ps = connection.prepareStatement(DELETE_PREREQUISITES)) {
                ps.setInt(1, campaignId);
                ps.setInt(2, lessonId);
                ps.executeUpdate();
            }

            // 2. Insert new ones
            if (prerequisiteIds != null) {
                try (PreparedStatement ps = connection.prepareStatement(INSERT_PREREQUISITE)) {
                    for (Integer prereq : prerequisiteIds) {
                        ps.setInt(1, campaignId);
                        ps.setInt(2, lessonId);
                        ps.setInt(3, prereq);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating lesson prerequisites", e);
            try {
                connection.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Rollback failed", ex);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    public List<PersistentCampaignLesson> getCampaignLessons(int campaignId) {
        List<PersistentCampaignLesson> result = new ArrayList<>();
        Connection connection = dbConnection.getConnection();

        try (PreparedStatement ps = connection.prepareStatement(SELECT_CAMPAIGN_LESSONS)) {
            ps.setInt(1, campaignId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int lessonId = rs.getInt("lesson_id");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                boolean optional = rs.getBoolean("optional");
                boolean extra = rs.getBoolean("extra");
                boolean boss = rs.getBoolean("boss");
                boolean important = rs.getBoolean("important");

                result.add(new PersistentCampaignLesson(
                        campaignId, lessonId, x, y, optional, extra, boss, important
                ));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving campaign lessons", e);
        }

        return result;
    }


    public List<Integer> getPrerequisites(int campaignId, int lessonId) {
        List<Integer> prereqs = new ArrayList<>();
        Connection connection = dbConnection.getConnection();

        try (PreparedStatement ps = connection.prepareStatement(SELECT_PREREQUISITES)) {
            ps.setInt(1, campaignId);
            ps.setInt(2, lessonId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                prereqs.add(rs.getInt("prerequisite_lesson_id"));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving prerequisites", e);
        }

        return prereqs;
    }

    public boolean deleteCampaignLesson(int campaignId, int lessonId, int userId) {
        Connection connection = dbConnection.getConnection();

        try {
            // ---------------------------------------------------------
            // 0. Check author
            // ---------------------------------------------------------
            try (PreparedStatement ps = connection.prepareStatement(CHECK_CAMPAIGN_AUTHOR)) {
                ps.setInt(1, campaignId);
                ps.setInt(2, userId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    LOGGER.log(Level.INFO, "User " + userId +
                            " is not the author of campaign " + campaignId);
                    return false;
                }
            }

            connection.setAutoCommit(false);

            // ---------------------------------------------------------
            // 1. Delete prerequisites where this lesson is dependent
            // ---------------------------------------------------------
            int removedDependent;
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM `playshogi`.`ps_campaign_lesson_prerequisite` " +
                            "WHERE `campaign_id` = ? AND `lesson_id` = ?;"
            )) {
                ps.setInt(1, campaignId);
                ps.setInt(2, lessonId);
                removedDependent = ps.executeUpdate();
            }

            // ---------------------------------------------------------
            // 2. Delete prerequisites where this lesson is *a prerequisite*
            // ---------------------------------------------------------
            int removedAsPrereq;
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM `playshogi`.`ps_campaign_lesson_prerequisite` " +
                            "WHERE `campaign_id` = ? AND `prerequisite_lesson_id` = ?;"
            )) {
                ps.setInt(1, campaignId);
                ps.setInt(2, lessonId);
                removedAsPrereq = ps.executeUpdate();
            }

            // ---------------------------------------------------------
            // 3. Delete campaign lesson entry
            // ---------------------------------------------------------
            int removedLesson;
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM `playshogi`.`ps_campaign_lesson` " +
                            "WHERE `campaign_id` = ? AND `lesson_id` = ?;"
            )) {
                ps.setInt(1, campaignId);
                ps.setInt(2, lessonId);
                removedLesson = ps.executeUpdate();
            }

            // If lesson row wasn't removed → lesson not in campaign
            if (removedLesson == 0) {
                connection.rollback();
                LOGGER.log(Level.INFO, "Lesson " + lessonId +
                        " was not found in campaign " + campaignId);
                return false;
            }

            connection.commit();

            LOGGER.log(Level.INFO, "Deleted lesson " + lessonId +
                    " from campaign " + campaignId +
                    " (" + removedDependent + " prereqs, " +
                    removedAsPrereq + " dependents removed)");
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting campaign lesson", e);
            try {
                connection.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Rollback failed", ex);
            }
            return false;

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    public CampaignGraph getFullCampaignGraph(int campaignId, int userId) {

        Connection connection = dbConnection.getConnection();

        // --- Step 0: Load campaign metadata ---
        String title;
        String description;

        try (PreparedStatement ps = connection.prepareStatement(SELECT_CAMPAIGN_INFO)) {
            ps.setInt(1, campaignId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                title = rs.getString("title");
                description = rs.getString("description");
            } else {
                LOGGER.log(Level.WARNING, "Campaign not found: " + campaignId);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving campaign info", e);
            return null;
        }

        // --- Step 1: Load campaign lessons (Now with User Progress) ---
        List<CampaignLessonNode> nodeList = new ArrayList<>();
        Map<String, CampaignLessonNode> nodeLookup = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(SELECT_CAMPAIGN_LESSONS_WITH_PROGRESS)) {
            ps.setInt(1, userId);       // Set the user ID for the LEFT JOIN
            ps.setInt(2, campaignId);   // Set the campaign ID
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CampaignLessonNode node = new CampaignLessonNode();
                String lessonId = String.valueOf(rs.getInt("lesson_id"));
                node.setLessonId(lessonId);
                node.setX(rs.getInt("x"));
                node.setY(rs.getInt("y"));
                node.setTitle(rs.getString("title"));
                node.setDifficulty(SqlUtils.getInteger(rs, "difficulty"));
                node.setDraft(rs.getBoolean("draft"));
                node.setImportant(rs.getBoolean("important"));
                node.setOptional(rs.getBoolean("optional"));
                node.setExtra(rs.getBoolean("extra"));
                node.setBoss(rs.getBoolean("boss"));
                node.setCompleted(rs.getBoolean("complete"));
                node.setSkipped(node.isCompleted() && rs.getInt("percentage") < 100);

                nodeList.add(node);
                nodeLookup.put(lessonId, node);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving campaign lessons", e);
            return null;
        }

        // --- Step 2: Load ALL prerequisites ---
        Map<Integer, List<String>> prereqMap = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL_PREREQUISITES_FOR_CAMPAIGN)) {
            ps.setInt(1, campaignId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int lessonId = rs.getInt("lesson_id");
                int prereqId = rs.getInt("prerequisite_lesson_id");

                prereqMap
                        .computeIfAbsent(lessonId, k -> new ArrayList<>())
                        .add(String.valueOf(prereqId));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving prerequisites", e);
            return null;
        }

        // --- Step 3: Build final graph ---
        for (CampaignLessonNode node : nodeList) {
            List<String> prereqs = prereqMap.getOrDefault(Integer.parseInt(node.getLessonId()), new ArrayList<>());
            node.setPrerequisites(prereqs);

            // Logic: Set locked to true if ANY prerequisite is neither complete nor skipped
            boolean isLocked = false;
            for (String prereqId : prereqs) {
                CampaignLessonNode prereqNode = nodeLookup.get(prereqId);

                // If the prerequisite exists and is NOT finished, lock this node
                if (prereqNode != null && !prereqNode.isCompleted() && !prereqNode.isSkipped()) {
                    isLocked = true;
                    break;
                }
            }
            node.setLocked(isLocked);
        }

        return new CampaignGraph(String.valueOf(campaignId), title, description, nodeList);
    }

    private static final String INSERT_CHAPTER =
            "INSERT INTO ps_lesson_chapter " +
                    "(lesson_id, kifu_id, type, title, chapter_number, orientation, hidden) " +
                    "SELECT ?, ?, ?, ?, COALESCE(MAX(chapter_number), 0) + 1, ?, ? " +
                    "FROM ps_lesson_chapter WHERE lesson_id = ?";

    public boolean addChapter(final int lessonId, final int kifuId, final int type,
                              final String title, final int orientation, final boolean hidden) {

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CHAPTER)) {

            preparedStatement.setInt(1, lessonId);
            preparedStatement.setInt(2, kifuId);
            preparedStatement.setByte(3, (byte) type); // type is TINYINT
            preparedStatement.setString(4, title);
            preparedStatement.setByte(5, (byte) orientation); // orientation is TINYINT
            preparedStatement.setBoolean(6, hidden); // hidden is TINYINT(1) / BOOLEAN
            preparedStatement.setInt(7, lessonId);

            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "Added chapter " + kifuId + " to lesson: " + lessonId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding new chapter to lesson " + lessonId, e);
            return false;
        }
        return false;
    }

    // Updates all fields except the lesson_id (which should not change) and the chapter_id.
// It joins with ps_lessons to verify the userId is the author.
    private static final String UPDATE_CHAPTER =
            "UPDATE ps_lesson_chapter c " +
                    "JOIN ps_lessons l ON c.lesson_id = l.id " +
                    "SET c.kifu_id = ?, c.type = ?, c.title = ?, " +
                    "    c.orientation = ?, c.hidden = ? " +
                    "WHERE c.chapter_id = ? AND l.author_id = ?";

    public boolean updateChapter(final int chapterId, final int userId, final int newKifuId,
                                 final int newType, final String newTitle,
                                 final int newOrientation, final boolean newHidden) {

        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CHAPTER)) {

            // SET values (1 through 5)
            preparedStatement.setInt(1, newKifuId);
            preparedStatement.setByte(2, (byte) newType);
            preparedStatement.setString(3, newTitle);
            preparedStatement.setByte(4, (byte) newOrientation);
            preparedStatement.setBoolean(5, newHidden);

            // WHERE clause values (6 and 7)
            preparedStatement.setInt(6, chapterId);
            preparedStatement.setInt(7, userId); // Verified against the lesson author

            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "Updated chapter: " + chapterId);
                return true;
            } else {
                LOGGER.log(Level.INFO, "Did not find chapter: " + chapterId + " for authorized user " + userId);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating chapter " + chapterId + " in db", e);
            return false;
        }
    }

    // Deletes the chapter, joining ps_lessons to verify the userId is the author.
    private static final String DELETE_CHAPTER =
            "DELETE c FROM ps_lesson_chapter c " +
                    "JOIN ps_lessons l ON c.lesson_id = l.id " +
                    "WHERE c.chapter_id = ? AND l.author_id = ?";

    public boolean deleteChapter(final int chapterId, final int userId) {
        Connection connection = dbConnection.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CHAPTER)) {

            preparedStatement.setInt(1, chapterId);
            preparedStatement.setInt(2, userId); // Verified against the lesson author

            int rs = preparedStatement.executeUpdate();
            if (rs == 1) {
                LOGGER.log(Level.INFO, "Deleted chapter: " + chapterId);
                return true;
            } else {
                LOGGER.log(Level.INFO, "Did not find chapter: " + chapterId + " for authorized user " + userId);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting chapter " + chapterId + " from db", e);
            return false;
        }
    }

    private static final String CHECK_LESSON_AUTHOR =
            "SELECT 1 FROM ps_lessons WHERE id = ? AND author_id = ?";

    public boolean isLessonAuthor(final int lessonId, final int userId) {
        Connection connection = dbConnection.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(CHECK_LESSON_AUTHOR)) {

            preparedStatement.setInt(1, lessonId);
            preparedStatement.setInt(2, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error checking authorship for lesson " + lessonId + " by user " + userId, e);
            return false;
        }

        LOGGER.log(Level.INFO, "User {0} is NOT the author of lesson {1}.", new Object[]{userId, lessonId});
        return false;
    }

    private static final String FIND_CHAPTERS_BY_LESSON_ID =
            "SELECT c.chapter_id, c.lesson_id, c.kifu_id, c.type, c.title, c.chapter_number, c.orientation, c.hidden," +
                    " k.usf " +
                    "FROM ps_lesson_chapter c " +
                    "INNER JOIN ps_kifu k ON c.kifu_id = k.id " + // Join to ps_kifu
                    "WHERE c.lesson_id = ? " +
                    "ORDER BY c.chapter_number ASC";

    public List<LessonChapterDto> listLessonChapters(final int lessonId) {
        List<LessonChapterDto> chapters = new ArrayList<>();
        Connection connection = dbConnection.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_CHAPTERS_BY_LESSON_ID)) {

            preparedStatement.setInt(1, lessonId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    LessonChapterDto chapter = new LessonChapterDto();
                    chapter.setChapterId(String.valueOf(resultSet.getInt("chapter_id")));
                    chapter.setLessonId(String.valueOf(resultSet.getInt("lesson_id")));
                    chapter.setKifuId(String.valueOf(resultSet.getInt("kifu_id")));
                    chapter.setType(resultSet.getByte("type"));
                    chapter.setTitle(resultSet.getString("title"));
                    chapter.setOrientation(resultSet.getByte("orientation"));
                    chapter.setHidden(resultSet.getBoolean("hidden"));
                    chapter.setKifuUsf(resultSet.getString("usf"));

                    chapters.add(chapter);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving chapters for lesson " + lessonId, e);
            return chapters;
        }

        LOGGER.log(Level.FINE, "Retrieved {0} chapters for lesson {1}.", new Object[]{chapters.size(), lessonId});
        return chapters;
    }

    public boolean swapChapterOrder(final int chapterId1, final int chapterId2, final int userId) {
        Connection connection = dbConnection.getConnection();

        // SQL to fetch current numbers and ensure they belong to the same lesson
        final String SELECT_CHAPTERS =
                "SELECT c.chapter_id, c.lesson_id, c.chapter_number " +
                        "FROM ps_lesson_chapter c " +
                        "JOIN ps_lessons l ON c.lesson_id = l.id " +
                        "WHERE c.chapter_id IN (?, ?) AND l.author_id = ?";

        // SQL to update a single chapter number
        final String UPDATE_NUMBER =
                "UPDATE ps_lesson_chapter SET chapter_number = ? WHERE chapter_id = ?";

        try {
            // --- 1. Auto-commit off for Transaction ---
            connection.setAutoCommit(false);

            // --- 2. Verify Authorization and Fetch Current Numbers ---
            Map<Integer, Integer> chapterMap = new HashMap<>(); // Key: chapterId, Value: chapterNumber
            int lessonId = -1;

            try (PreparedStatement selectStmt = connection.prepareStatement(SELECT_CHAPTERS)) {
                selectStmt.setInt(1, chapterId1);
                selectStmt.setInt(2, chapterId2);
                selectStmt.setInt(3, userId);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        int currentChapterId = rs.getInt("chapter_id");
                        int currentLessonId = rs.getInt("lesson_id");

                        // Sanity check: Ensure both chapters belong to the same lesson (critical for a swap)
                        if (lessonId == -1) {
                            lessonId = currentLessonId;
                        } else if (lessonId != currentLessonId) {
                            throw new SQLException("Cannot swap chapters from different lessons.");
                        }

                        chapterMap.put(currentChapterId, rs.getInt("chapter_number"));
                    }
                }
            }

            // Check if exactly two unique, authorized chapters were found
            if (chapterMap.size() != 2) {
                throw new SQLException("Failed to find both chapters or authorization failed.");
            }

            // Extract the two numbers
            int num1 = chapterMap.get(chapterId1);
            int num2 = chapterMap.get(chapterId2);

            // Check if the numbers are already the same (nothing to do)
            if (num1 == num2) {
                connection.rollback();
                LOGGER.log(Level.INFO, "Chapters {0} and {1} already have the same chapter number {2}.",
                        new Object[]{chapterId1, chapterId2, num1});
                return true;
            }

            // --- 3. Perform the Swap (Three-Step Shuffle) ---

            final int TEMP_NUMBER = 0;

            // Step A: Move Chapter 1 to Temp
            try (PreparedStatement updateStmt = connection.prepareStatement(UPDATE_NUMBER)) {
                updateStmt.setInt(1, TEMP_NUMBER);
                updateStmt.setInt(2, chapterId1);
                updateStmt.executeUpdate();
            }

            // Step B: Move Chapter 2 to Number 1
            try (PreparedStatement updateStmt = connection.prepareStatement(UPDATE_NUMBER)) {
                updateStmt.setInt(1, num1);
                updateStmt.setInt(2, chapterId2);
                updateStmt.executeUpdate();
            }

            // Step C: Move Chapter 1 (currently Temp) to Number 2
            try (PreparedStatement updateStmt = connection.prepareStatement(UPDATE_NUMBER)) {
                updateStmt.setInt(1, num2);
                updateStmt.setInt(2, chapterId1);
                updateStmt.executeUpdate();
            }

            // --- 4. Commit Transaction ---
            connection.commit();
            LOGGER.log(Level.INFO, "Swapped chapter numbers for {0} (was {1}) and {2} (was {3}).",
                    new Object[]{chapterId1, num1, chapterId2, num2});
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback();
                LOGGER.log(Level.WARNING, "Transaction rolled back due to error during chapter swap.", e);
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Rollback failed.", rollbackEx);
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true); // Restore default
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to restore auto-commit.", e);
            }
        }
    }

}
