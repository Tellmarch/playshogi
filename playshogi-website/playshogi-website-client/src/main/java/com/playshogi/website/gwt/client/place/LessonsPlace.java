package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class LessonsPlace extends Place {

    public LessonsPlace() {
    }

    @Prefix("Lessons")
    public static class Tokenizer implements PlaceTokenizer<LessonsPlace> {

        @Override
        public String getToken(final LessonsPlace place) {
            return null;
        }

        @Override
        public LessonsPlace getPlace(final String token) {
            return new LessonsPlace();
        }

    }

}
