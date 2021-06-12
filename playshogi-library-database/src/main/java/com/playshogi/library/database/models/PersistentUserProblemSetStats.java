package com.playshogi.library.database.models;

import java.util.Date;

public class PersistentUserProblemSetStats {

    private final int userId;
    private final String userName;
    private final int collectionId;
    private final Date attemptedDate;
    private final Integer timeSpentMs;
    private final boolean complete;
    private final int solved;

    public PersistentUserProblemSetStats(int userId, final String userName, int collectionId, Date attemptedDate,
                                         Integer timeSpentMs,
                                         boolean complete, final int solved) {
        this.userId = userId;
        this.userName = userName;
        this.collectionId = collectionId;
        this.attemptedDate = attemptedDate;
        this.timeSpentMs = timeSpentMs;
        this.complete = complete;
        this.solved = solved;
    }

    public PersistentUserProblemSetStats(int userId, int collectionId, Integer timeSpentMs, boolean complete,
                                         final int solved) {
        this(userId, null, collectionId, null, timeSpentMs, complete, solved);
    }

    public int getUserId() {
        return userId;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public Date getAttemptedDate() {
        return attemptedDate;
    }

    public Integer getTimeSpentMs() {
        return timeSpentMs;
    }

    public boolean getComplete() {
        return complete;
    }

    public int getSolved() {
        return solved;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "PersistentUserProblemSetStats{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", problemId=" + collectionId +
                ", attemptedDate=" + attemptedDate +
                ", timeSpentMs=" + timeSpentMs +
                ", complete=" + complete +
                ", solved=" + solved +
                '}';
    }
}
