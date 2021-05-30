package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ManageLessonsPlace extends Place {

    public ManageLessonsPlace() {
    }

    @Prefix("ManageLessons")
    public static class Tokenizer implements PlaceTokenizer<ManageLessonsPlace> {

        @Override
        public String getToken(final ManageLessonsPlace place) {
            return null;
        }

        @Override
        public ManageLessonsPlace getPlace(final String token) {
            return new ManageLessonsPlace();
        }

    }

}
