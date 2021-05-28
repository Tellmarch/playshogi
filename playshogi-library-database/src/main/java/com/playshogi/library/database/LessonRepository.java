package com.playshogi.library.database;

import com.playshogi.library.database.models.PersistentLesson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LessonRepository {
    private static final Logger LOGGER = Logger.getLogger(LessonRepository.class.getName());

    private static final String SELECT_VISIBLE_LESSONS = "SELECT * FROM `playshogi`.`ps_lessons` WHERE hidden = 0;";

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

                result.add(new PersistentLesson(id, kifuId, parentId, title, description, tags, previewSfen,
                        difficulty, likes, authorId, hidden, creationDate, updateDate, type, index));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving visible lessons in db", e);
        }
        return result;
    }


}
