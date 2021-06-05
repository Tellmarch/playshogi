package com.playshogi.website.gwt.client.events.puzzles;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class UserJumpedToProblemEvent extends GenericEvent {
    private final int problemIndex;

    public UserJumpedToProblemEvent(final int problemIndex) {
        this.problemIndex = problemIndex;
    }

    public int getProblemIndex() {
        return problemIndex;
    }
}
