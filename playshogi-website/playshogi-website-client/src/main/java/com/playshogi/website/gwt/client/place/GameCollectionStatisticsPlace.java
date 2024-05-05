package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class GameCollectionStatisticsPlace extends Place {

    private final String collectionId;

    public GameCollectionStatisticsPlace(final String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    @Prefix("GameCollectionStatistics")
    public static class Tokenizer implements PlaceTokenizer<GameCollectionStatisticsPlace> {

        @Override
        public String getToken(final GameCollectionStatisticsPlace place) {
            return place.collectionId;
        }

        @Override
        public GameCollectionStatisticsPlace getPlace(final String token) {
            return new GameCollectionStatisticsPlace(token);
        }

    }

}
