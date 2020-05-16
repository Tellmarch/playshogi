package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class UserFinishedProblemEvent extends GenericEvent {

    private final boolean success;

    public UserFinishedProblemEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
