package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import java.util.Optional;

public class CollectionPlace extends Place {

    private final String collectionId;

    public CollectionPlace() {
        this(null);
    }

    public CollectionPlace(final String collectionId) {
        if (collectionId == null || "null".equals(collectionId)) {
            this.collectionId = null;
        } else {
            this.collectionId = collectionId;
        }
    }

    public Optional<String> getCollectionId() {
        return Optional.ofNullable(collectionId);
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
