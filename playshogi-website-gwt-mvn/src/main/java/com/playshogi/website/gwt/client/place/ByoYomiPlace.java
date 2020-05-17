package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ByoYomiPlace extends Place {

    public ByoYomiPlace() {
    }

    @Prefix("ByoYomi")
    public static class Tokenizer implements PlaceTokenizer<ByoYomiPlace> {

        @Override
        public String getToken(final ByoYomiPlace place) {
            return null;
        }

        @Override
        public ByoYomiPlace getPlace(final String token) {
            return new ByoYomiPlace();
        }

    }
}
