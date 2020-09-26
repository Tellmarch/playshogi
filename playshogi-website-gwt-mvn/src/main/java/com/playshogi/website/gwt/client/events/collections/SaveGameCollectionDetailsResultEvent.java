package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class SaveGameCollectionDetailsResultEvent extends GenericEvent {

    private final boolean success;

    public SaveGameCollectionDetailsResultEvent(final boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
