package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class MainPagePlace extends Place {

    public MainPagePlace() {
    }

    @Prefix("Main")
    public static class Tokenizer implements PlaceTokenizer<MainPagePlace> {

        @Override
        public String getToken(final MainPagePlace place) {
            return null;
        }

        @Override
        public MainPagePlace getPlace(final String token) {
            return new MainPagePlace();
        }

    }

}
