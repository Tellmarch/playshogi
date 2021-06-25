package com.playshogi.website.gwt.client.events.gametree;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.moves.EditMove;

public class EditMovePlayedEvent extends GenericEvent {
    private final EditMove move;

    public EditMovePlayedEvent(final EditMove move) {
        this.move = move;
    }

    public EditMove getMove() {
        return move;
    }

}
