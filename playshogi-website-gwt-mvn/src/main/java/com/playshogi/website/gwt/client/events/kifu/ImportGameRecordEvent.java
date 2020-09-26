package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.models.record.GameRecord;

public class ImportGameRecordEvent extends GenericEvent {
    private final GameRecord gameRecord;
    private final String collectionId;

    public ImportGameRecordEvent(final GameRecord gameRecord, final String collectionId) {
        this.gameRecord = gameRecord;
        this.collectionId = collectionId;
    }

    public GameRecord getGameRecord() {
        return gameRecord;
    }

    public String getCollectionId() {
        return collectionId;
    }
}
