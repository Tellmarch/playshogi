package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class GameCollectionDetailsList implements Serializable {
    private GameCollectionDetails[] myCollections;
    private GameCollectionDetails[] publicCollections;

    public GameCollectionDetailsList() {
    }

    public GameCollectionDetails[] getMyCollections() {
        return myCollections;
    }

    public void setMyCollections(final GameCollectionDetails[] myCollections) {
        this.myCollections = myCollections;
    }

    public GameCollectionDetails[] getPublicCollections() {
        return publicCollections;
    }

    public void setPublicCollections(final GameCollectionDetails[] publicCollections) {
        this.publicCollections = publicCollections;
    }
}
