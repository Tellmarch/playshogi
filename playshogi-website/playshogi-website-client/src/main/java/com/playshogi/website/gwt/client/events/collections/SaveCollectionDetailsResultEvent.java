package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class SaveCollectionDetailsResultEvent extends GenericEvent {

    private final boolean success;

    public SaveCollectionDetailsResultEvent(final boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
