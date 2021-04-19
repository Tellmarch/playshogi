package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ManageProblemsPlace extends Place {

    public ManageProblemsPlace() {
    }

    @Prefix("ManageProblems")
    public static class Tokenizer implements PlaceTokenizer<ManageProblemsPlace> {

        @Override
        public String getToken(final ManageProblemsPlace place) {
            return null;
        }

        @Override
        public ManageProblemsPlace getPlace(final String token) {
            return new ManageProblemsPlace();
        }

    }

}
