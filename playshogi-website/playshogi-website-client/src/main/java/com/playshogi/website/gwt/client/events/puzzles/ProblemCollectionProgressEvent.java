package com.playshogi.website.gwt.client.events.puzzles;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.client.models.ProblemStatus;

import java.util.Arrays;

public class ProblemCollectionProgressEvent extends GenericEvent {

    private final int problemIndex;
    private final ProblemStatus[] statuses;

    public ProblemCollectionProgressEvent(final int problemIndex, final ProblemStatus[] statuses) {
        this.problemIndex = problemIndex;
        this.statuses = statuses;
    }

    public int getProblemIndex() {
        return problemIndex;
    }

    public ProblemStatus[] getStatuses() {
        return statuses;
    }

    @Override
    public String toString() {
        return "ProblemCollectionProgressEvent{" +
                "problemIndex=" + problemIndex +
                ", statuses=" + Arrays.toString(statuses) +
                '}';
    }
}
