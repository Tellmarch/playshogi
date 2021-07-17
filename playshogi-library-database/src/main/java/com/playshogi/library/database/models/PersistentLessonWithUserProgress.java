package com.playshogi.library.database.models;

public class PersistentLessonWithUserProgress {

    private final PersistentLesson persistentLesson;
    private final PersistentUserLessonProgress persistentUserLessonProgress;

    public PersistentLessonWithUserProgress(final PersistentLesson persistentLesson,
                                            final PersistentUserLessonProgress persistentUserLessonProgress) {
        this.persistentLesson = persistentLesson;
        this.persistentUserLessonProgress = persistentUserLessonProgress;
    }

    public PersistentLesson getPersistentLesson() {
        return persistentLesson;
    }

    public PersistentUserLessonProgress getPersistentUserLessonProgress() {
        return persistentUserLessonProgress;
    }

    @Override
    public String toString() {
        return "PersistentLessonWithUserProgress{" +
                "persistentLesson=" + persistentLesson +
                ", persistentUserLessonProgress=" + persistentUserLessonProgress +
                '}';
    }
}
