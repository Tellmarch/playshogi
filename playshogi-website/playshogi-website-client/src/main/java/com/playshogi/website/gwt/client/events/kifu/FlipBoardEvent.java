package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class FlipBoardEvent extends GenericEvent {
    private final boolean inverted;

    public FlipBoardEvent(final boolean inverted) {
        this.inverted = inverted;
    }

    public boolean isInverted() {
        return inverted;
    }

    @Override
    public String toString() {
        return "FlipBoardEvent{" +
                "inverted=" + inverted +
                '}';
    }
}
