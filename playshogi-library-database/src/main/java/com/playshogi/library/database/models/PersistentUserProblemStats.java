package com.playshogi.library.database.models;

import java.util.Date;
import java.util.Objects;

public class PersistentUserProblemStats {

    private final int userId;
    private final int problemId;
    private final Date lastSolved;
    private final int lastSolvingTime;

    public PersistentUserProblemStats(int userId, int problemId, Date lastSolved, int lastSolvingTime) {
        this.userId = userId;
        this.problemId = problemId;
        this.lastSolved = lastSolved;
        this.lastSolvingTime = lastSolvingTime;
    }

    public int getUserId() {
        return userId;
    }

    public int getProblemId() {
        return problemId;
    }

    public Date getLastSolved() {
        return lastSolved;
    }

    public int getLastSolvingTime() {
        return lastSolvingTime;
    }

    @Override
    public String toString() {
        return "PersistentUserProblemStats{" +
                "userId=" + userId +
                ", problemId=" + problemId +
                ", lastSolved=" + lastSolved +
                ", lastSolvingTime=" + lastSolvingTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersistentUserProblemStats that = (PersistentUserProblemStats) o;
        return userId == that.userId &&
                problemId == that.problemId &&
                lastSolvingTime == that.lastSolvingTime &&
                Objects.equals(lastSolved, that.lastSolved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, problemId, lastSolved, lastSolvingTime);
    }
}
