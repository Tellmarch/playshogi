package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class AdminCollectionsPlace extends Place {

    public AdminCollectionsPlace() {
    }

    @Prefix("AdminCollections")
    public static class Tokenizer implements PlaceTokenizer<AdminCollectionsPlace> {

        @Override
        public String getToken(final AdminCollectionsPlace place) {
            return null;
        }

        @Override
        public AdminCollectionsPlace getPlace(final String token) {
            return new AdminCollectionsPlace();
        }

    }

}
