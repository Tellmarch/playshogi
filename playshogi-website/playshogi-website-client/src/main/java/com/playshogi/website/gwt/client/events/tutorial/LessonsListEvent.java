package com.playshogi.website.gwt.client.events.tutorial;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.LessonDetails;

public class LessonsListEvent extends GenericEvent {
    private final LessonDetails[] lessons;

    public LessonsListEvent(final LessonDetails[] lessons) {
        this.lessons = lessons;
    }

    public LessonDetails[] getLessons() {
        return lessons;
    }
}
