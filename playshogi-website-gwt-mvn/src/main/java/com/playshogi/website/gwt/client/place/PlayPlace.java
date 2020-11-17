package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlayPlace extends Place {

    public PlayPlace() {
    }

    @Prefix("Play")
    public static class Tokenizer implements PlaceTokenizer<PlayPlace> {

        @Override
        public String getToken(final PlayPlace place) {
            return null;
        }

        @Override
        public PlayPlace getPlace(final String token) {
            return new PlayPlace();
        }

    }

}
