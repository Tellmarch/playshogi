package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.AnalysisRequestResult;
import com.playshogi.website.gwt.shared.models.AnalysisRequestStatus;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;

public class KifuEvaluationEvent extends GenericEvent {
    private final AnalysisRequestResult result;

    public KifuEvaluationEvent(AnalysisRequestResult result) {
        this.result = result;
    }

    public AnalysisRequestResult getResult() {
        return result;
    }

    public PositionEvaluationDetails[] getPositionEvaluationDetails() {
        return result.getDetails();
    }

    public AnalysisRequestStatus getStatus() {
        return result.getStatus();
    }

    public int getQueuePosition() {
        return result.getQueuePosition();
    }
}
