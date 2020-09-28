package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class RemoveGameFromCollectionEvent extends GenericEvent {

    private final String gameId;
    private final String collectionId;

    public RemoveGameFromCollectionEvent(final String gameId, final String collectionId) {
        this.gameId = gameId;
        this.collectionId = collectionId;
    }

    public String getGameId() {
        return gameId;
    }

    public String getCollectionId() {
        return collectionId;
    }
}
