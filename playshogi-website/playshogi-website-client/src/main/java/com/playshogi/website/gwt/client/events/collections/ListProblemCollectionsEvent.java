package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;

public class ListProblemCollectionsEvent extends GenericEvent {

    private final ProblemCollectionDetails[] publicCollections;
    private final ProblemCollectionDetails[] myCollections;

    public ListProblemCollectionsEvent(final ProblemCollectionDetails[] publicCollections,
                                       final ProblemCollectionDetails[] myCollections) {
        this.publicCollections = publicCollections;
        this.myCollections = myCollections;
    }

    public ProblemCollectionDetails[] getPublicCollections() {
        return publicCollections;
    }

    public ProblemCollectionDetails[] getMyCollections() {
        return myCollections;
    }
}
