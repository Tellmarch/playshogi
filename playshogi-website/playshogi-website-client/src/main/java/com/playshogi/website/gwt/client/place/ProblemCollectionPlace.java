package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ProblemCollectionPlace extends Place {

    private final String collectionId;

    public ProblemCollectionPlace(final String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    @Prefix("ProblemCollection")
    public static class Tokenizer implements PlaceTokenizer<ProblemCollectionPlace> {

        @Override
        public String getToken(final ProblemCollectionPlace place) {
            return place.collectionId;
        }

        @Override
        public ProblemCollectionPlace getPlace(final String token) {
            return new ProblemCollectionPlace(token);
        }

    }

}
