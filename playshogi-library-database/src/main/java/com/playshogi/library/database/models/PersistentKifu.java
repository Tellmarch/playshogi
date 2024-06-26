package com.playshogi.library.database.models;

import com.playshogi.library.shogi.models.record.GameRecord;

import java.util.Date;

public class PersistentKifu {

    public enum KifuType {
        GAME(1, "Game"), PROBLEM(2, "Problem"), LESSON(3, "Lesson");

        private final int dbInt;
        private final String description;

        KifuType(final int dbInt, final String description) {
            this.dbInt = dbInt;
            this.description = description;
        }

        public int getDbInt() {
            return dbInt;
        }

        public String getDescription() {
            return description;
        }

        public static KifuType fromDbInt(final int dbInt) {
            switch (dbInt) {
                case 1:
                    return GAME;
                case 2:
                    return PROBLEM;
                case 3:
                    return LESSON;
                default:
                    throw new IllegalArgumentException("Unknown kifu type: " + dbInt);
            }

        }
    }

    private final int id;
    private final String name;
    //TODO: probably remove this field, or at least not compute it when not necessary
    private final GameRecord kifu;
    private final String kifuUsf;
    private final Date creationDate;
    private final Date updateDate;
    private final KifuType type;
    private final int authorId;

    public PersistentKifu(final int id, final String name, final GameRecord kifu, final String kifuUsf,
                          final Date creationDate,
                          final Date updateDate, final KifuType type,
                          final int authorId) {
        this.id = id;
        this.name = name;
        this.kifu = kifu;
        this.kifuUsf = kifuUsf;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.type = type;
        this.authorId = authorId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GameRecord getKifu() {
        return kifu;
    }

    public String getKifuUsf() {
        return kifuUsf;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public KifuType getType() {
        return type;
    }

    public int getAuthorId() {
        return authorId;
    }

    @Override
    public String toString() {
        return "PersistentKifu{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", kifu=" + kifu +
                ", kifuUsf=" + kifuUsf +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                ", type=" + type +
                ", authorId=" + authorId +
                '}';
    }
}
