package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;

public class SaveGameCollectionDetailsEvent extends GenericEvent {

    private final GameCollectionDetails details;

    public SaveGameCollectionDetailsEvent(final GameCollectionDetails details) {
        this.details = details;
    }

    public GameCollectionDetails getDetails() {
        return details;
    }
}
