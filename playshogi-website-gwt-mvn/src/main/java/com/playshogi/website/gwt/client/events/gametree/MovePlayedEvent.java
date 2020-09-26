package com.playshogi.website.gwt.client.events.gametree;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.moves.ShogiMove;

public class MovePlayedEvent extends GenericEvent {
    private final ShogiMove move;

    public MovePlayedEvent(final ShogiMove move) {
        this.move = move;
    }

    public ShogiMove getMove() {
        return move;
    }

}
