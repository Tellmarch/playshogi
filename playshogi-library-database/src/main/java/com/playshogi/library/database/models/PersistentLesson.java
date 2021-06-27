package com.playshogi.library.database.models;

import java.util.Arrays;
import java.util.Date;

public class PersistentLesson {

    public enum LessonType {
        UNSPECIFIED(0, "Lesson"),
        LECTURE(1, "Lecture"),
        PRACTICE(2, "Practice");

        private final int dbInt;
        private final String description;

        LessonType(final int dbInt, final String description) {
            this.dbInt = dbInt;
            this.description = description;
        }

        public int getDbInt() {
            return dbInt;
        }

        public String getDescription() {
            return description;
        }

        public static LessonType fromDbInt(final int dbInt) {
            switch (dbInt) {
                case 0:
                    return UNSPECIFIED;
                case 1:
                    return LECTURE;
                case 2:
                    return PRACTICE;
                default:
                    throw new IllegalArgumentException("Unknown lesson type: " + dbInt);
            }

        }
    }

    private final int id;
    private final Integer kifuId;
    private final Integer problemCollectionId;
    private final Integer parentId;
    private final String title;
    private final String description;
    private final String[] tags;
    private final String previewSfen;
    private final Integer difficulty;
    private final int likes;
    private final Integer authorId;
    private final boolean hidden;
    private final Date createTime;
    private final Date updateTime;
    private final LessonType type;
    private final int index;

    public PersistentLesson(final int id, final Integer kifuId, final Integer problemCollectionId,
                            final Integer parentId, final String title,
                            final String description, final String[] tags, final String previewSfen,
                            final Integer difficulty, final int likes, final Integer authorId, final boolean hidden,
                            final Date createTime, final Date updateTime, final LessonType type, final int index) {
        this.id = id;
        this.kifuId = kifuId;
        this.problemCollectionId = problemCollectionId;
        this.parentId = parentId;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.previewSfen = previewSfen;
        this.difficulty = difficulty;
        this.likes = likes;
        this.authorId = authorId;
        this.hidden = hidden;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.type = type;
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public Integer getKifuId() {
        return kifuId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String[] getTags() {
        return tags;
    }

    public String getPreviewSfen() {
        return previewSfen;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public int getLikes() {
        return likes;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public boolean isHidden() {
        return hidden;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public LessonType getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public Integer getProblemCollectionId() {
        return problemCollectionId;
    }

    @Override
    public String toString() {
        return "PersistentLesson{" +
                "id=" + id +
                ", kifuId=" + kifuId +
                ", problemCollectionId=" + problemCollectionId +
                ", parentId=" + parentId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", tags='" + Arrays.toString(tags) + '\'' +
                ", previewSfen='" + previewSfen + '\'' +
                ", difficulty=" + difficulty +
                ", likes=" + likes +
                ", authorId=" + authorId +
                ", hidden=" + hidden +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", type=" + type +
                ", index=" + index +
                '}';
    }
}
