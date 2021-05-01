package com.playshogi.library.database.models;

import java.util.Date;
import java.util.Objects;

public class PersistentHighScore {
    private final int index;
    private final Integer userId;
    private final int score;
    private final Date date;
    private final String name;
    private final String event;

    public PersistentHighScore(final int index, final Integer userId, final int score, final Date date,
                               final String name
            , final String event) {
        this.index = index;
        this.userId = userId;
        this.score = score;
        this.date = date;
        this.name = name;
        this.event = event;
    }

    public int getIndex() {
        return index;
    }

    public Integer getUserId() {
        return userId;
    }

    public int getScore() {
        return score;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getEvent() {
        return event;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersistentHighScore that = (PersistentHighScore) o;
        return index == that.index && score == that.score && Objects.equals(userId, that.userId) && Objects.equals(date, that.date) && Objects.equals(name, that.name) && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, userId, score, date, name, event);
    }

    @Override
    public String toString() {
        return "PersistentHighScore{" +
                "index=" + index +
                ", userId=" + userId +
                ", score=" + score +
                ", date=" + date +
                ", name='" + name + '\'' +
                ", event='" + event + '\'' +
                '}';
    }
}
