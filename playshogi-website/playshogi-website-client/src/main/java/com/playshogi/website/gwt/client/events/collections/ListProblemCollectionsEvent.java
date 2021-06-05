package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;

public class ListProblemCollectionsEvent extends GenericEvent {

    private final ProblemCollectionDetails[] publicCollections;

    public ListProblemCollectionsEvent(final ProblemCollectionDetails[] publicCollections) {
        this.publicCollections = publicCollections;
    }

    public ProblemCollectionDetails[] getPublicCollections() {
        return publicCollections;
    }
}
