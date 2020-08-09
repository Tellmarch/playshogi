package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;

public class KifuEvaluationEvent extends GenericEvent {
    private final PositionEvaluationDetails[] positionEvaluationDetails;

    public KifuEvaluationEvent(PositionEvaluationDetails[] positionEvaluationDetails) {
        this.positionEvaluationDetails = positionEvaluationDetails;
    }

    public PositionEvaluationDetails[] getPositionEvaluationDetails() {
        return positionEvaluationDetails;
    }
}
