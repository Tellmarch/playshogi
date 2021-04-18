package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.record.GameInformation;

public class GameInformationChangedEvent extends GenericEvent {
    private final GameInformation gameInformation;

    public GameInformationChangedEvent(final GameInformation gameInformation) {
        this.gameInformation = gameInformation;
    }

    public GameInformation getGameInformation() {
        return gameInformation;
    }
}
