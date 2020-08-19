package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.client.widget.board.PieceGraphics;

public class PieceStyleSelectedEvent extends GenericEvent {
    private final PieceGraphics.Style style;

    public PieceStyleSelectedEvent(PieceGraphics.Style style) {
        this.style = style;
    }

    public PieceGraphics.Style getStyle() {
        return style;
    }
}
