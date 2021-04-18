package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.decorations.Arrow;

public class ArrowDrawnEvent extends GenericEvent {
    private final Arrow arrow;

    public ArrowDrawnEvent(final Arrow arrow) {
        this.arrow = arrow;
    }

    public Arrow getArrow() {
        return arrow;
    }
}
