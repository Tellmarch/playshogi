package com.playshogi.website.gwt.client.events.puzzles;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class ProblemsOrderSelectedEvent extends GenericEvent {

    private final boolean random;

    public ProblemsOrderSelectedEvent(boolean random) {
        this.random = random;
    }

    public boolean getRandom() {
        return random;
    }

}
