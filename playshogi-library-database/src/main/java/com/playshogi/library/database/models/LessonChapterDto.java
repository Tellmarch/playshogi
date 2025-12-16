package com.playshogi.library.database.models;

public class LessonChapterDto {
    private String chapterId; // Only needed for modification/deletion
    private String lessonId;
    private String kifuId;
    private String kifuUsf;
    private int type;
    private String title;
    private int chapterNumber;
    private int orientation;
    private boolean hidden;


    public LessonChapterDto() {
    }

    public LessonChapterDto(final String chapterId, final String lessonId, final String kifuId, final String kifuUsf,
                            final int type, final String title, final int chapterNumber, final int orientation,
                            final boolean hidden) {
        this.chapterId = chapterId;
        this.lessonId = lessonId;
        this.kifuId = kifuId;
        this.kifuUsf = kifuUsf;
        this.type = type;
        this.title = title;
        this.chapterNumber = chapterNumber;
        this.orientation = orientation;
        this.hidden = hidden;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(final String chapterId) {
        this.chapterId = chapterId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(final String lessonId) {
        this.lessonId = lessonId;
    }

    public String getKifuId() {
        return kifuId;
    }

    public void setKifuId(final String kifuId) {
        this.kifuId = kifuId;
    }

    public String getKifuUsf() {
        return kifuUsf;
    }

    public void setKifuUsf(final String kifuUsf) {
        this.kifuUsf = kifuUsf;
    }

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(final int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(final int orientation) {
        this.orientation = orientation;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return "LessonChapterDto{" +
                "chapterId='" + chapterId + '\'' +
                ", lessonId='" + lessonId + '\'' +
                ", kifuId='" + kifuId + '\'' +
                ", kifuUsf='" + kifuUsf + '\'' +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", chapterNumber=" + chapterNumber +
                ", orientation=" + orientation +
                ", hidden=" + hidden +
                '}';
    }
}
