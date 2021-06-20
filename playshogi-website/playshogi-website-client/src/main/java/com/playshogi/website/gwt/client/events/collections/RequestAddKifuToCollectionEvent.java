package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.KifuDetails;

public class RequestAddKifuToCollectionEvent extends GenericEvent {
    private String kifuId;
    private String collectionId;
    private final KifuDetails.KifuType type;

    public RequestAddKifuToCollectionEvent(final String kifuId, final String collectionId,
                                           final KifuDetails.KifuType type) {
        this.kifuId = kifuId;
        this.collectionId = collectionId;
        this.type = type;
    }

    public String getKifuId() {
        return kifuId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public KifuDetails.KifuType getType() {
        return type;
    }
}
