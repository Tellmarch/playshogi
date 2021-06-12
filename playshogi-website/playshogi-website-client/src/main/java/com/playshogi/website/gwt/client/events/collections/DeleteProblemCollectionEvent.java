package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class DeleteProblemCollectionEvent extends GenericEvent {

    private final String collectionId;

    public DeleteProblemCollectionEvent(final String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionId() {
        return collectionId;
    }
}
