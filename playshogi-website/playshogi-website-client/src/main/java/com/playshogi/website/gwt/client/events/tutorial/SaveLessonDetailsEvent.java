package com.playshogi.website.gwt.client.events.tutorial;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.LessonDetails;

public class SaveLessonDetailsEvent extends GenericEvent {

    private final LessonDetails details;

    public SaveLessonDetailsEvent(final LessonDetails details) {
        this.details = details;
    }

    public LessonDetails getDetails() {
        return details;
    }
}
