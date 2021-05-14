package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class MyCollectionsPlace extends Place {

    public MyCollectionsPlace() {
    }

    @Prefix("MyCollections")
    public static class Tokenizer implements PlaceTokenizer<MyCollectionsPlace> {

        @Override
        public String getToken(final MyCollectionsPlace place) {
            return null;
        }

        @Override
        public MyCollectionsPlace getPlace(final String token) {
            return new MyCollectionsPlace();
        }

    }

}
