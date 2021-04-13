package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class AnalysisRequestResult implements Serializable {
    private AnalysisRequestStatus status = AnalysisRequestStatus.UNAVAILABLE;
    private PositionEvaluationDetails[] evaluationDetails = null;
    private GameInsightsDetails gameInsightsDetails = null;
    private int queuePosition = 0;

    public AnalysisRequestResult() {
    }

    public AnalysisRequestResult(final AnalysisRequestStatus status) {
        this.status = status;
    }

    public AnalysisRequestStatus getStatus() {
        return status;
    }

    public void setStatus(final AnalysisRequestStatus status) {
        this.status = status;
    }

    public PositionEvaluationDetails[] getEvaluationDetails() {
        return evaluationDetails;
    }

    public void setEvaluationDetails(final PositionEvaluationDetails[] evaluationDetails) {
        this.evaluationDetails = evaluationDetails;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(final int queuePosition) {
        this.queuePosition = queuePosition;
    }

    public GameInsightsDetails getGameInsightsDetails() {
        return gameInsightsDetails;
    }

    public void setGameInsightsDetails(final GameInsightsDetails gameInsightsDetails) {
        this.gameInsightsDetails = gameInsightsDetails;
    }

    @Override
    public String toString() {
        return "AnalysisRequestResult{" +
                "status=" + status +
                ", evaluationDetails=" + Arrays.toString(evaluationDetails) +
                ", gameInsightsDetails=" + gameInsightsDetails +
                ", queuePosition=" + queuePosition +
                '}';
    }
}
