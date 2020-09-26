package com.playshogi.website.gwt.client.events.tutorial;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class ChangeTutorialTitleEvent extends GenericEvent {

    private final String text;

    public ChangeTutorialTitleEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
