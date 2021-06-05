package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ProblemCollectionsPlace extends Place {

    public ProblemCollectionsPlace() {
    }

    @Prefix("ProblemCollections")
    public static class Tokenizer implements PlaceTokenizer<ProblemCollectionsPlace> {

        @Override
        public String getToken(final ProblemCollectionsPlace place) {
            return null;
        }

        @Override
        public ProblemCollectionsPlace getPlace(final String token) {
            return new ProblemCollectionsPlace();
        }

    }

}
