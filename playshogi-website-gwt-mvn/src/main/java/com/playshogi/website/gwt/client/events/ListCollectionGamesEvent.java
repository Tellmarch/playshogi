package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.KifuDetails;

public class ListCollectionGamesEvent extends GenericEvent {

    private final KifuDetails[] details;

    public ListCollectionGamesEvent(KifuDetails[] details) {
        this.details = details;
    }

    public KifuDetails[] getDetails() {
        return details;
    }
}
