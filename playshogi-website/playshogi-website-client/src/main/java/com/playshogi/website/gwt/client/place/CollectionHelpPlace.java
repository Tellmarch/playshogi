package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CollectionHelpPlace extends Place {

    public CollectionHelpPlace() {
    }

    @Prefix("CollectionHelp")
    public static class Tokenizer implements PlaceTokenizer<CollectionHelpPlace> {

        @Override
        public String getToken(final CollectionHelpPlace place) {
            return null;
        }

        @Override
        public CollectionHelpPlace getPlace(final String token) {
            return new CollectionHelpPlace();
        }

    }

}
