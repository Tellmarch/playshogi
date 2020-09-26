package com.playshogi.website.gwt.client.events.gametree;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.models.record.GameTree;

public class GameTreeChangedEvent extends GenericEvent {
    private final GameTree gameTree;
    private final int goToMove;

    public GameTreeChangedEvent(final GameTree gameTree) {
        this(gameTree, 0);
    }

    public GameTreeChangedEvent(final GameTree gameTree, final int goToMove) {
        this.gameTree = gameTree;
        this.goToMove = goToMove;
    }

    public GameTree getGameTree() {
        return gameTree;
    }

    public int getGoToMove() {
        return goToMove;
    }
}
