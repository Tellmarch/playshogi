package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CollectionPagePlace extends Place {

    public CollectionPagePlace() {
    }

    @Prefix("Collection")
    public static class Tokenizer implements PlaceTokenizer<CollectionPagePlace> {

        @Override
        public String getToken(final CollectionPagePlace place) {
            return null;
        }

        @Override
        public CollectionPagePlace getPlace(final String token) {
            return new CollectionPagePlace();
        }

    }

}
