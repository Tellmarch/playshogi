package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ProblemEditorPlace extends Place {

    public ProblemEditorPlace() {
    }

    @Prefix("ProblemEditor")
    public static class Tokenizer implements PlaceTokenizer<ProblemEditorPlace> {

        @Override
        public String getToken(final ProblemEditorPlace place) {
            return null;
        }

        @Override
        public ProblemEditorPlace getPlace(final String token) {
            return new ProblemEditorPlace();
        }

    }

}
