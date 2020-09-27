package com.playshogi.website.gwt.client.events.gametree;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class MoveSelectedEvent extends GenericEvent {
    private final int moveNumber;

    public MoveSelectedEvent(final int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public int getMoveNumber() {
        return moveNumber;
    }
}
