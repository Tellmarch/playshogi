package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;

import java.util.Arrays;

public class ListGameCollectionsEvent extends GenericEvent {

    private final GameCollectionDetails[] myCollections;
    private final GameCollectionDetails[] publicCollections;

    public ListGameCollectionsEvent(final GameCollectionDetails[] myCollections,
                                    final GameCollectionDetails[] publicCollections) {
        this.myCollections = myCollections;
        this.publicCollections = publicCollections;
    }

    public GameCollectionDetails[] getMyCollections() {
        return myCollections;
    }

    public GameCollectionDetails[] getPublicCollections() {
        return publicCollections;
    }

    @Override
    public String toString() {
        return "ListGameCollectionsEvent{" +
                "myCollections=" + Arrays.toString(myCollections) +
                ", publicCollections=" + Arrays.toString(publicCollections) +
                '}';
    }
}
