package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class ByoYomiSurvivalFinishedEvent extends GenericEvent {

    private int finalScore;
    private int solved;
    private int failed;
    private int totalTimeSec;

    public ByoYomiSurvivalFinishedEvent(int finalScore, int solved, int failed, int totalTimeSec) {
        this.finalScore = finalScore;
        this.solved = solved;
        this.failed = failed;
        this.totalTimeSec = totalTimeSec;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public int getSolved() {
        return solved;
    }

    public int getFailed() {
        return failed;
    }

    public int getTotalTimeSec() {
        return totalTimeSec;
    }
}
