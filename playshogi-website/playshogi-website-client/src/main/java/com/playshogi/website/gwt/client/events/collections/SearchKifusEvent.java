package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.record.GameResult;

public class SearchKifusEvent extends GenericEvent {
    private final GameResult result;
    private final String player;
    private final String partialPositionSfen;

    public SearchKifusEvent(final GameResult result, String player, String partialPositionSfen) {
        this.result = result;
        this.player = player;
        this.partialPositionSfen = partialPositionSfen;
    }

    public GameResult getResult() {
        return result;
    }

    public String getPlayer() {
        return player;
    }

    public String getPartialPositionSfen() {
        return partialPositionSfen;
    }
}
