package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import com.playshogi.website.gwt.shared.models.ProblemDetails;

public class ListCollectionProblemsEvent extends GenericEvent {

    private final ProblemDetails[] details;
    private final ProblemCollectionDetails collectionDetails;

    public ListCollectionProblemsEvent(final ProblemDetails[] details,
                                       final ProblemCollectionDetails collectionDetails) {
        this.details = details;
        this.collectionDetails = collectionDetails;
    }

    public ProblemDetails[] getDetails() {
        return details;
    }

    public ProblemCollectionDetails getCollectionDetails() {
        return collectionDetails;
    }
}
