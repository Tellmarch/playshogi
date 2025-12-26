package com.playshogi.library.database.models;

import java.util.List;

public class CampaignLessonNode {
    private String lessonId;
    private String title;
    private int x;
    private int y;
    private Integer difficulty;
    private List<String> prerequisites;
    private boolean locked;
    private boolean completed;
    private boolean skipped;
    private boolean draft;
    private boolean important;
    private boolean optional;
    private boolean extra;
    private boolean boss;

    public CampaignLessonNode() {
    }

    public CampaignLessonNode(final String lessonId, final String title, final int x, final int y,
                              final Integer difficulty, final List<String> prerequisites, final boolean locked,
                              final boolean completed, final boolean skipped, final boolean draft,
                              final boolean important, final boolean optional, final boolean extra,
                              final boolean boss) {
        this.lessonId = lessonId;
        this.title = title;
        this.x = x;
        this.y = y;
        this.difficulty = difficulty;
        this.prerequisites = prerequisites;
        this.locked = locked;
        this.completed = completed;
        this.skipped = skipped;
        this.draft = draft;
        this.important = important;
        this.optional = optional;
        this.extra = extra;
        this.boss = boss;
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

    public boolean isLocked() {
        return locked;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public boolean isDraft() {
        return draft;
    }

    public boolean isImportant() {
        return important;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isExtra() {
        return extra;
    }

    public boolean isBoss() {
        return boss;
    }

    public void setLessonId(final String lessonId) {
        this.lessonId = lessonId;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public void setDifficulty(final Integer difficulty) {
        this.difficulty = difficulty;
    }

    public void setPrerequisites(final List<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

    public void setCompleted(final boolean completed) {
        this.completed = completed;
    }

    public void setSkipped(final boolean skipped) {
        this.skipped = skipped;
    }

    public void setDraft(final boolean draft) {
        this.draft = draft;
    }

    public void setImportant(final boolean important) {
        this.important = important;
    }

    public void setOptional(final boolean optional) {
        this.optional = optional;
    }

    public void setExtra(final boolean extra) {
        this.extra = extra;
    }

    public void setBoss(final boolean boss) {
        this.boss = boss;
    }

    @Override
    public String toString() {
        return "CampaignLessonNode{" +
                "lessonId='" + lessonId + '\'' +
                ", title='" + title + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", difficulty=" + difficulty +
                ", prerequisites=" + prerequisites +
                ", locked=" + locked +
                ", completed=" + completed +
                ", skipped=" + skipped +
                ", draft=" + draft +
                ", important=" + important +
                ", optional=" + optional +
                ", extra=" + extra +
                ", boss=" + boss +
                '}';
    }
}
