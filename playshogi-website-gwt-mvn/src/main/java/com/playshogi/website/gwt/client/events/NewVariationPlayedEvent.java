package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class NewVariationPlayedEvent extends GenericEvent {

    private final boolean positionCheckmate;

    public NewVariationPlayedEvent(boolean positionCheckmate) {

        this.positionCheckmate = positionCheckmate;
    }

    public boolean isPositionCheckmate() {
        return positionCheckmate;
    }
}
