package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.KifuDetails;

public class ListCollectionGamesEvent extends GenericEvent {

    private final KifuDetails[] details;
    private final GameCollectionDetails collectionDetails;

    public ListCollectionGamesEvent(final KifuDetails[] details, final GameCollectionDetails collectionDetails) {
        this.details = details;
        this.collectionDetails = collectionDetails;
    }

    public KifuDetails[] getDetails() {
        return details;
    }

    public GameCollectionDetails getCollectionDetails() {
        return collectionDetails;
    }
}
