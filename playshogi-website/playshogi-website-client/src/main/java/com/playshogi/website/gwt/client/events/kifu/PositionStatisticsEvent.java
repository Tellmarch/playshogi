package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.shared.models.PositionDetails;

public class PositionStatisticsEvent extends GenericEvent {

    private final ShogiPosition shogiPosition;
    private final String gameSetId;
    private final PositionDetails positionDetails;

    public PositionStatisticsEvent(final PositionDetails positionDetails, final ShogiPosition shogiPosition,
                                   final String gameSetId) {
        this.positionDetails = positionDetails;
        this.shogiPosition = shogiPosition;
        this.gameSetId = gameSetId;
    }

    public PositionDetails getPositionDetails() {
        return positionDetails;
    }

    public ShogiPosition getShogiPosition() {
        return shogiPosition;
    }

    public String getGameSetId() {
        return gameSetId;
    }
}
