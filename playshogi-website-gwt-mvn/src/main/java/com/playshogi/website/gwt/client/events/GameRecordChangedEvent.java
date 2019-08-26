package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.models.record.GameRecord;

public class GameRecordChangedEvent extends GenericEvent {
    private final GameRecord gameRecord;

    public GameRecordChangedEvent(final GameRecord gameRecord) {
        this.gameRecord = gameRecord;
    }

    public GameRecord getGameRecord() {
        return gameRecord;
    }
}
