package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CollectionPlace extends Place {

    private final String collectionId;

    public CollectionPlace(final String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    @Prefix("Collection")
    public static class Tokenizer implements PlaceTokenizer<CollectionPlace> {

        @Override
        public String getToken(final CollectionPlace place) {
            return place.collectionId;
        }

        @Override
        public CollectionPlace getPlace(final String token) {
            return new CollectionPlace(token);
        }

    }

}
