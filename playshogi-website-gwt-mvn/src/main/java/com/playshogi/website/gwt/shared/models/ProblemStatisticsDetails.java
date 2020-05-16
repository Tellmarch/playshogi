package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Date;

public class ProblemStatisticsDetails implements Serializable {

    private int problemId;
    private Date attemptedDate;
    private Integer timeSpentMs;
    private Boolean correct;

    public ProblemStatisticsDetails() {
    }

    public ProblemStatisticsDetails(int problemId, Date attemptedDate, Integer timeSpentMs, Boolean correct) {
        this.problemId = problemId;
        this.attemptedDate = attemptedDate;
        this.timeSpentMs = timeSpentMs;
        this.correct = correct;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public Date getAttemptedDate() {
        return attemptedDate;
    }

    public void setAttemptedDate(Date attemptedDate) {
        this.attemptedDate = attemptedDate;
    }

    public Integer getTimeSpentMs() {
        return timeSpentMs;
    }

    public void setTimeSpentMs(Integer timeSpentMs) {
        this.timeSpentMs = timeSpentMs;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    @Override
    public String toString() {
        return "ProblemStatisticsDetails{" +
                "problemId=" + problemId +
                ", attemptedDate=" + attemptedDate +
                ", timeSpentMs=" + timeSpentMs +
                ", correct=" + correct +
                '}';
    }
}
