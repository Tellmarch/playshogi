package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PublicCollectionsPlace extends Place {

    public PublicCollectionsPlace() {
    }

    @Prefix("PublicCollections")
    public static class Tokenizer implements PlaceTokenizer<PublicCollectionsPlace> {

        @Override
        public String getToken(final PublicCollectionsPlace place) {
            return null;
        }

        @Override
        public PublicCollectionsPlace getPlace(final String token) {
            return new PublicCollectionsPlace();
        }

    }

}
