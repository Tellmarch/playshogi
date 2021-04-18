package com.playshogi.website.gwt.client.events.gametree;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class EndOfVariationReachedEvent extends GenericEvent {

    private final boolean mainLine;

    public EndOfVariationReachedEvent(boolean mainLine) {
        this.mainLine = mainLine;
    }

    public boolean isMainLine() {
        return mainLine;
    }
}
