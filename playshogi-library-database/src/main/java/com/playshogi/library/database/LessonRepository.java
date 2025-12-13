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

    private static final String SELECT_VISIBLE_LESSONS = "SELECT * FROM `playshogi`.`ps_lessons` WHERE hidden = 0;";
    private static final String SELECT_VISIBLE_LESSONS_WITH_USER_PROGRESS =
            "SELECT * FROM `playshogi`.`ps_lessons`" +
                    "LEFT JOIN (SELECT * FROM `playshogi`.`ps_userlessonsprogress` WHERE user_id = ?) p " +
                    "ON (id = lesson_id) WHERE hidden = 0;";
    private static final String SELECT_ALL_LESSONS = "SELECT * FROM `playshogi`.`ps_lessons`;";
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

    private static final String SELECT_CAMPAIGN_LESSONS_WITH_DETAILS =
            "SELECT cl.lesson_id, cl.x, cl.y, cl.optional, cl.extra, cl.boss, cl.important, " +
                    "       l.title, l.difficulty " +
                    "FROM `playshogi`.`ps_campaign_lesson` cl " +
                    "JOIN `playshogi`.`ps_lessons` l ON cl.lesson_id = l.id " +
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

    public void updateCampaignLesson(PersistentCampaignLesson cLesson) {
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

            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating campaign lesson", e);
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

    public CampaignGraph getFullCampaignGraph(int campaignId) {

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

        // --- Step 1: Load campaign lessons ---
        class LessonTmp {
            int id, x, y;
            String title;
            Integer difficulty;
        }
        List<LessonTmp> lessonList = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(SELECT_CAMPAIGN_LESSONS_WITH_DETAILS)) {
            ps.setInt(1, campaignId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LessonTmp tmp = new LessonTmp();
                tmp.id = rs.getInt("lesson_id");
                tmp.x = rs.getInt("x");
                tmp.y = rs.getInt("y");
                tmp.title = rs.getString("title");
                tmp.difficulty = SqlUtils.getInteger(rs, "difficulty");
                lessonList.add(tmp);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving campaign lessons", e);
            return null;
        }

        // --- Step 2: Load ALL prerequisites (one query!) ---
        Map<Integer, List<Integer>> prereqMap = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL_PREREQUISITES_FOR_CAMPAIGN)) {
            ps.setInt(1, campaignId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int lessonId = rs.getInt("lesson_id");
                int prereqId = rs.getInt("prerequisite_lesson_id");

                prereqMap
                        .computeIfAbsent(lessonId, k -> new ArrayList<>())
                        .add(prereqId);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving prerequisites", e);
            return null;
        }

        // --- Step 3: Build final graph ---
        List<CampaignLessonNode> nodes = new ArrayList<>();

        for (LessonTmp tmp : lessonList) {
            List<Integer> prereqs = prereqMap.getOrDefault(tmp.id, new ArrayList<>());

            nodes.add(new CampaignLessonNode(
                    tmp.id,
                    tmp.title,
                    tmp.x,
                    tmp.y,
                    tmp.difficulty,
                    prereqs
            ));
        }

        return new CampaignGraph(campaignId, title, description, nodes);
    }

}
