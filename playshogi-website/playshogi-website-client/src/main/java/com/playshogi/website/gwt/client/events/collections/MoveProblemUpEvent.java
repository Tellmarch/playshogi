package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import com.playshogi.website.gwt.shared.models.ProblemDetails;

public class MoveProblemUpEvent extends GenericEvent {
    private ProblemDetails problemDetails;
    private ProblemCollectionDetails problemCollectionDetails;

    public MoveProblemUpEvent(final ProblemDetails problemDetails,
                              final ProblemCollectionDetails problemCollectionDetails) {
        this.problemDetails = problemDetails;
        this.problemCollectionDetails = problemCollectionDetails;
    }

    public ProblemDetails getProblemDetails() {
        return problemDetails;
    }

    public ProblemCollectionDetails getProblemCollectionDetails() {
        return problemCollectionDetails;
    }
}
