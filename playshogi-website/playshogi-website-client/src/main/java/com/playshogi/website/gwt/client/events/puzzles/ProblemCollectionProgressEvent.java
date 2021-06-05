package com.playshogi.website.gwt.client.events.puzzles;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.client.activity.ProblemsActivity;

import java.util.Arrays;

public class ProblemCollectionProgressEvent extends GenericEvent {

    private int problemIndex;
    private ProblemsActivity.ProblemStatus[] statuses;

    public ProblemCollectionProgressEvent(final int problemIndex, final ProblemsActivity.ProblemStatus[] statuses) {
        this.problemIndex = problemIndex;
        this.statuses = statuses;
    }

    public int getProblemIndex() {
        return problemIndex;
    }

    public ProblemsActivity.ProblemStatus[] getStatuses() {
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
