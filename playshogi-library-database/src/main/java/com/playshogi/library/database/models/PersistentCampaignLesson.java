package com.playshogi.library.database.models;

public class PersistentCampaignLesson {
    private final int campaignId;
    private final int lessonId;
    private final int x;
    private final int y;
    private final boolean optional;
    private final boolean extra;
    private final boolean boss;
    private final boolean important;

    public PersistentCampaignLesson(final int campaignId, final int lessonId, final int x, final int y,
                                    final boolean optional, final boolean extra, final boolean boss,
                                    final boolean important) {
        this.campaignId = campaignId;
        this.lessonId = lessonId;
        this.x = x;
        this.y = y;
        this.optional = optional;
        this.extra = extra;
        this.boss = boss;
        this.important = important;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public int getLessonId() {
        return lessonId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

    public boolean isImportant() {
        return important;
    }

    @Override
    public String toString() {
        return "PersistentCampaignLesson{" +
                "campaignId=" + campaignId +
                ", lessonId=" + lessonId +
                ", x=" + x +
                ", y=" + y +
                ", optional=" + optional +
                ", extra=" + extra +
                ", boss=" + boss +
                ", important=" + important +
                '}';
    }
}