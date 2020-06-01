package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ByoYomiLandingPlace extends Place {

    public ByoYomiLandingPlace() {
    }

    @Prefix("ByoYomiLanding")
    public static class Tokenizer implements PlaceTokenizer<ByoYomiLandingPlace> {

        @Override
        public String getToken(final ByoYomiLandingPlace place) {
            return null;
        }

        @Override
        public ByoYomiLandingPlace getPlace(final String token) {
            return new ByoYomiLandingPlace();
        }

    }

}
