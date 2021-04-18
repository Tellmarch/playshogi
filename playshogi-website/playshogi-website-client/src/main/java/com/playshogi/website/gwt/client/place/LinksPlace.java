package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class LinksPlace extends Place {

    public LinksPlace() {
    }

    @Prefix("Links")
    public static class Tokenizer implements PlaceTokenizer<LinksPlace> {

        @Override
        public String getToken(final LinksPlace place) {
            return null;
        }

        @Override
        public LinksPlace getPlace(final String token) {
            return new LinksPlace();
        }

    }

}
