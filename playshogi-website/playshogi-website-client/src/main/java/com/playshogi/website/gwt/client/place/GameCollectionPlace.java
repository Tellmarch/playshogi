package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class GameCollectionPlace extends Place {

    private final String collectionId;

    public GameCollectionPlace(final String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    @Prefix("GameCollection")
    public static class Tokenizer implements PlaceTokenizer<GameCollectionPlace> {

        @Override
        public String getToken(final GameCollectionPlace place) {
            return place.collectionId;
        }

        @Override
        public GameCollectionPlace getPlace(final String token) {
            return new GameCollectionPlace(token);
        }

    }

}
