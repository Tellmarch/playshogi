package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.AnalysisRequestResult;
import com.playshogi.website.gwt.shared.models.AnalysisRequestStatus;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;

public class KifuEvaluationEvent extends GenericEvent {
    private final AnalysisRequestResult result;
    private final String kifuId;

    public KifuEvaluationEvent(AnalysisRequestResult result, final String kifuId) {
        this.result = result;
        this.kifuId = kifuId;
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

    public String getKifuId() {
        return kifuId;
    }

    public int getQueuePosition() {
        return result.getQueuePosition();
    }
}
