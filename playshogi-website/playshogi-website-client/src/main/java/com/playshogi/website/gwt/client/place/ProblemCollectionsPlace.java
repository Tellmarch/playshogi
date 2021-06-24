package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ProblemCollectionsPlace extends Place {

    private final String search;

    public ProblemCollectionsPlace() {
        this(null);
    }

    public ProblemCollectionsPlace(final String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    @Prefix("ProblemCollections")
    public static class Tokenizer implements PlaceTokenizer<ProblemCollectionsPlace> {

        @Override
        public String getToken(final ProblemCollectionsPlace place) {
            return place.getSearch();
        }

        @Override
        public ProblemCollectionsPlace getPlace(final String token) {
            return new ProblemCollectionsPlace(token);
        }

    }

}
