package com.playshogi.website.gwt.client.events.puzzles;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.ProblemStatisticsDetails;

public class ProblemStatisticsEvent extends GenericEvent {

    private final ProblemStatisticsDetails[] details;

    public ProblemStatisticsEvent(ProblemStatisticsDetails[] details) {
        this.details = details;
    }

    public ProblemStatisticsDetails[] getDetails() {
        return details;
    }
}
