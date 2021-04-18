package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.KifuDetails;

public class ListKifusEvent extends GenericEvent {
    private final KifuDetails[] kifus;

    public ListKifusEvent(final KifuDetails[] kifus) {
        this.kifus = kifus;
    }

    public KifuDetails[] getKifus() {
        return kifus;
    }
}
