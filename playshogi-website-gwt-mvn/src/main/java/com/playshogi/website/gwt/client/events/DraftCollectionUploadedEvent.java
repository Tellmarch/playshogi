package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class DraftCollectionUploadedEvent extends GenericEvent {

    private final String id;

    public DraftCollectionUploadedEvent(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
