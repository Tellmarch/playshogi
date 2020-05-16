package com.playshogi.library.database.models;

import java.util.Date;
import java.util.Objects;

public class PersistentUserProblemStats {

    private final int userId;
    private final int problemId;
    private final Date attemptedDate;
    private final Integer timeSpentMs;
    private final Boolean correct;

    public PersistentUserProblemStats(int userId, int problemId, Date attemptedDate, Integer timeSpentMs,
                                      Boolean correct) {
        this.userId = userId;
        this.problemId = problemId;
        this.attemptedDate = attemptedDate;
        this.timeSpentMs = timeSpentMs;
        this.correct = correct;
    }

    public int getUserId() {
        return userId;
    }

    public int getProblemId() {
        return problemId;
    }

    public Date getAttemptedDate() {
        return attemptedDate;
    }

    public Integer getTimeSpentMs() {
        return timeSpentMs;
    }

    public Boolean getCorrect() {
        return correct;
    }

    @Override
    public String toString() {
        return "PersistentUserProblemStats{" +
                "userId=" + userId +
                ", problemId=" + problemId +
                ", attemptedDate=" + attemptedDate +
                ", timeSpentMs=" + timeSpentMs +
                ", correct=" + correct +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersistentUserProblemStats that = (PersistentUserProblemStats) o;
        return userId == that.userId &&
                problemId == that.problemId &&
                Objects.equals(attemptedDate, that.attemptedDate) &&
                Objects.equals(timeSpentMs, that.timeSpentMs) &&
                Objects.equals(correct, that.correct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, problemId, attemptedDate, timeSpentMs, correct);
    }
}
