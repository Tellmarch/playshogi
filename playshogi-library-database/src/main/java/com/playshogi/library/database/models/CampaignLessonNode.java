package com.playshogi.library.database.models;

import java.util.List;

public class CampaignLessonNode {
    private final String lessonId;
    private final String title;
    private final int x;
    private final int y;
    private final Integer difficulty;
    private final List<String> prerequisites;

    public CampaignLessonNode(String lessonId, String title, int x, int y,
                              Integer difficulty, List<String> prerequisites) {
        this.lessonId = lessonId;
        this.title = title;
        this.x = x;
        this.y = y;
        this.difficulty = difficulty;
        this.prerequisites = prerequisites;
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getTitle() {
        return title;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    @Override
    public String toString() {
        return "CampaignLessonNode{" +
                "lessonId=" + lessonId +
                ", title='" + title + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", difficulty=" + difficulty +
                ", prerequisites=" + prerequisites +
                '}';
    }
}
