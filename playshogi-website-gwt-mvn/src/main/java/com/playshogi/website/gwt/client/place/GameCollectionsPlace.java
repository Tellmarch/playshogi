package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class GameCollectionsPlace extends Place {

    public GameCollectionsPlace() {
    }

    @Prefix("Games")
    public static class Tokenizer implements PlaceTokenizer<GameCollectionsPlace> {

        @Override
        public String getToken(final GameCollectionsPlace place) {
            return null;
        }

        @Override
        public GameCollectionsPlace getPlace(final String token) {
            return new GameCollectionsPlace();
        }

    }

}
