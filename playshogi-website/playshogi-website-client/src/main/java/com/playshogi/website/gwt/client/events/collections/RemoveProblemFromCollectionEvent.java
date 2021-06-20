package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class RemoveProblemFromCollectionEvent extends GenericEvent {

    private final String problemId;
    private final String collectionId;

    public RemoveProblemFromCollectionEvent(final String problemId, final String collectionId) {
        this.problemId = problemId;
        this.collectionId = collectionId;
    }

    public String getProblemId() {
        return problemId;
    }

    public String getCollectionId() {
        return collectionId;
    }
}
