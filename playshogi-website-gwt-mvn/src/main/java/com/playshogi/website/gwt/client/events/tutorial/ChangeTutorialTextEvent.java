package com.playshogi.website.gwt.client.events.tutorial;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class ChangeTutorialTextEvent extends GenericEvent {

    private final String text;

    public ChangeTutorialTextEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
