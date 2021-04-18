package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class UserKifusPlace extends Place {

    public UserKifusPlace() {
    }

    @Prefix("UserKifus")
    public static class Tokenizer implements PlaceTokenizer<UserKifusPlace> {

        @Override
        public String getToken(final UserKifusPlace place) {
            return null;
        }

        @Override
        public UserKifusPlace getPlace(final String token) {
            return new UserKifusPlace();
        }

    }

}
