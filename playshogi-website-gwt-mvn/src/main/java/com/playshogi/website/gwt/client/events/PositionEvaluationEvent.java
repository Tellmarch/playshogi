package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class PositionEvaluationEvent extends GenericEvent {

    private final String evaluation;

    public PositionEvaluationEvent(String evaluation) {
        this.evaluation = evaluation;
    }

    public String getEvaluation() {
        return evaluation;
    }
}
