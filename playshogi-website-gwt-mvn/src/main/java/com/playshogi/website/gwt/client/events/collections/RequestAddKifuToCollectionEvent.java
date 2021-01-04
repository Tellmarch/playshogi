package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class RequestAddKifuToCollectionEvent extends GenericEvent {
    private String kifuId;
    private String collectionId;

    public RequestAddKifuToCollectionEvent(final String kifuId, final String collectionId) {
        this.kifuId = kifuId;
        this.collectionId = collectionId;
    }

    public String getKifuId() {
        return kifuId;
    }

    public String getCollectionId() {
        return collectionId;
    }
}
