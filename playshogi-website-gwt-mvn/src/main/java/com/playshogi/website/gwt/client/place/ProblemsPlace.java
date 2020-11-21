package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ProblemsPlace extends Place {
    private final String collectionId;
    private final int problemIndex;

    public ProblemsPlace(final String collectionId, final int problemIndex) {
        this.collectionId = collectionId;
        this.problemIndex = problemIndex;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public int getProblemIndex() {
        return problemIndex;
    }

    @Prefix("Problems")
    public static class Tokenizer implements PlaceTokenizer<ProblemsPlace> {

        @Override
        public String getToken(final ProblemsPlace place) {
            return place.getCollectionId() + ":" + place.getProblemIndex();
        }

        @Override
        public ProblemsPlace getPlace(final String token) {
            String[] split = token.split(":");
            return new ProblemsPlace(split[0], Integer.parseInt(split[1]));
        }

    }
}
