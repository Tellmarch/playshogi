package com.playshogi.library.database.models;

import java.util.Date;

public class PersistentUserLessonProgress {

    private final int userId;
    private final int lessonId;
    private final Date timeViewed;
    private final Integer timeSpentMs;
    private final boolean complete;
    private final int percentage;
    private final Integer rating;

    public PersistentUserLessonProgress(final int userId, final int lessonId, final Date timeViewed,
                                        final Integer timeSpentMs, boolean complete, final int percentage,
                                        final Integer rating) {
        this.userId = userId;
        this.lessonId = lessonId;
        this.timeViewed = timeViewed;
        this.timeSpentMs = timeSpentMs;
        this.complete = complete;
        this.percentage = percentage;
        this.rating = rating;
    }

    public int getUserId() {
        return userId;
    }

    public int getLessonId() {
        return lessonId;
    }

    public Date getTimeViewed() {
        return timeViewed;
    }

    public Integer getTimeSpentMs() {
        return timeSpentMs;
    }

    public boolean getComplete() {
        return complete;
    }

    public int getPercentage() {
        return percentage;
    }

    public Integer getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "PersistentUserLessonProgress{" +
                "userId=" + userId +
                ", lessonId=" + lessonId +
                ", viewedDate=" + timeViewed +
                ", timeSpentMs=" + timeSpentMs +
                ", complete=" + complete +
                ", percentage=" + percentage +
                ", rating=" + rating +
                '}';
    }
}
