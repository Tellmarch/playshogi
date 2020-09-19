package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class AnalysisRequestResult implements Serializable {
    private AnalysisRequestStatus status = AnalysisRequestStatus.UNAVAILABLE;
    private PositionEvaluationDetails[] details = null;
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

    public PositionEvaluationDetails[] getDetails() {
        return details;
    }

    public void setDetails(final PositionEvaluationDetails[] details) {
        this.details = details;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(final int queuePosition) {
        this.queuePosition = queuePosition;
    }

    @Override
    public String toString() {
        return "AnalysisRequestResult{" +
                "status=" + status +
                ", details=" + Arrays.toString(details) +
                ", queuePosition=" + queuePosition +
                '}';
    }
}
