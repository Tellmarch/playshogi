package com.playshogi.website.gwt.shared.models;

public enum AnalysisRequestStatus {
    IN_PROGRESS,
    COMPLETED,
    QUEUED,
    QUEUE_TOO_LONG,
    USER_QUOTA_EXCEEDED,
    NOT_ALLOWED,
    NOT_REQUESTED,
    UNAVAILABLE;

    public boolean needToWait() {
        return this == IN_PROGRESS || this == QUEUED;
    }

    public boolean isFinal() {
        return !needToWait();
    }

    public boolean isDenied() {
        return this == UNAVAILABLE || this == NOT_ALLOWED || this == USER_QUOTA_EXCEEDED || this == QUEUE_TOO_LONG;
    }
}
