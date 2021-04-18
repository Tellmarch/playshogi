package com.playshogi.website.gwt.client.events.puzzles;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class UserFinishedProblemEvent extends GenericEvent {

    private final boolean success;
    private final String problemId;

    public UserFinishedProblemEvent(boolean success, String problemId) {
        this.success = success;
        this.problemId = problemId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getProblemId() {
        return problemId;
    }
}
