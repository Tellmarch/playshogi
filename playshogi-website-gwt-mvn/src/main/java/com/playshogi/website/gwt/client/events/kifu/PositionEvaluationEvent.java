package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;

public class PositionEvaluationEvent extends GenericEvent {

    private final PositionEvaluationDetails evaluation;

    public PositionEvaluationEvent(PositionEvaluationDetails evaluation) {
        this.evaluation = evaluation;
    }

    public PositionEvaluationDetails getEvaluation() {
        return evaluation;
    }
}
