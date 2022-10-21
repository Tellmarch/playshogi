package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class BlindModeEvent extends GenericEvent {
    private final boolean blind;

    public BlindModeEvent(final boolean blind) {
        this.blind = blind;
    }

    public boolean isBlind() {
        return blind;
    }

    @Override
    public String toString() {
        return "BlindModeEvent{" +
                "blind=" + blind +
                '}';
    }
}
