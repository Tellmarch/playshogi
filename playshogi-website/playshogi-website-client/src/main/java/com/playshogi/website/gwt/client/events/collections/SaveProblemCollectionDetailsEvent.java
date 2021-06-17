package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;

public class SaveProblemCollectionDetailsEvent extends GenericEvent {

    private final ProblemCollectionDetails details;

    public SaveProblemCollectionDetailsEvent(final ProblemCollectionDetails details) {
        this.details = details;
    }

    public ProblemCollectionDetails getDetails() {
        return details;
    }
}
