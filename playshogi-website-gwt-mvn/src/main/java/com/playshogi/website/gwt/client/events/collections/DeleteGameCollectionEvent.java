package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class DeleteGameCollectionEvent extends GenericEvent {

    private final String collectionId;

    public DeleteGameCollectionEvent(final String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionId() {
        return collectionId;
    }
}
