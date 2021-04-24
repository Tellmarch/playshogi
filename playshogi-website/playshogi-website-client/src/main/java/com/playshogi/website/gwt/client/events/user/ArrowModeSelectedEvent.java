package com.playshogi.website.gwt.client.events.user;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class ArrowModeSelectedEvent extends GenericEvent {
    private boolean enabled;

    public ArrowModeSelectedEvent(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
