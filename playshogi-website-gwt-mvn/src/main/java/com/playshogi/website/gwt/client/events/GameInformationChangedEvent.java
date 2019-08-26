package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.models.record.GameInformation;

public class GameInformationChangedEvent extends GenericEvent {
    private final GameInformation gameInformation;

    public GameInformationChangedEvent(final GameInformation gameInformation) {
        this.gameInformation = gameInformation;
    }

    public GameInformation getGameInformation() {
        return gameInformation;
    }
}
