package com.playshogi.library.database;

import com.playshogi.library.database.models.PersistentLesson;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.playshogi.library.database.SqlUtils.setInteger;

public class LessonRepository {
    private static final Logger LOGGER = Logger.getLogger(LessonRepository.class.getName());

    private static final String SELECT_VISIBLE_LESSONS = "SELECT * FROM `playshogi`.`ps_lessons` WHERE hidden = 0;";
    private static final String SELECT_ALL_LESSONS = "SELECT * FROM `playshogi`.`ps_lessons`;";
    private static final String INSERT_LESSON = "INSERT INTO `playshogi`.`ps_lessons` (`kifu_id`, `parent_id`, " +
            "`title`, `description`, `tags`, `preview_sfen`, `difficulty`, `author_id`, `hidden`, " +
            "`type`, `index`, `problemset_id`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_LESSON = "UPDATE `playshogi`.`ps_lessons` SET `kifu_id` = ?, `parent_id` = ?, " +
            "`title` = ?, `description` = ?, `tags` = ?, `preview_sfen` = ?, `difficulty` = ?, `author_id` = ?, " +
            "`hidden` = ?, " +
            "`type` = ?, `index` = ?, `problemset_id` = ? WHERE `id` = ?;";

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
}
