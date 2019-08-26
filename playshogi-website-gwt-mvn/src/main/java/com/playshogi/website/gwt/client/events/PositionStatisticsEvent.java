package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.shared.models.PositionDetails;

public class PositionStatisticsEvent extends GenericEvent {

    private final ShogiPosition shogiPosition;
    private final PositionDetails positionDetails;

    public PositionStatisticsEvent(final PositionDetails positionDetails, final ShogiPosition shogiPosition) {
        this.positionDetails = positionDetails;
        this.shogiPosition = shogiPosition;
    }

    public PositionDetails getPositionDetails() {
        return positionDetails;
    }

    public ShogiPosition getShogiPosition() {
        return shogiPosition;
    }

}
