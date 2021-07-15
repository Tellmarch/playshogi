package com.playshogi.website.gwt.client.events.gametree;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class NewVariationPlayedEvent extends GenericEvent {

    private final ShogiPosition newPosition;

    public NewVariationPlayedEvent(final ShogiPosition newPosition) {
        this.newPosition = newPosition;
    }

    public ShogiPosition getNewPosition() {
        return newPosition;
    }
}
