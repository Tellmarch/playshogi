package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class ProblemNumMovesSelectedEvent extends GenericEvent {

    private final int numMoves;

    public ProblemNumMovesSelectedEvent(int numMoves) {
        this.numMoves = numMoves;
    }

    public int getNumMoves() {
        return numMoves;
    }
}
