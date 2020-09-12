package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;

public class GameCollectionsEvent extends GenericEvent {

    private final GameCollectionDetails[] details;

    public GameCollectionsEvent(GameCollectionDetails[] details) {
        this.details = details;
    }

    public GameCollectionDetails[] getDetails() {
        return details;
    }
}
