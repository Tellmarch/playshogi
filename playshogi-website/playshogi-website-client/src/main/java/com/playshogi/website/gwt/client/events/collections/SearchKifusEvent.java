package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.record.GameResult;

public class SearchKifusEvent extends GenericEvent {
    private final GameResult result;

    private final String player;

    public SearchKifusEvent(final GameResult result, String player) {
        this.result = result;
        this.player = player;
    }

    public GameResult getResult() {
        return result;
    }

    public String getPlayer() {
        return player;
    }
}
