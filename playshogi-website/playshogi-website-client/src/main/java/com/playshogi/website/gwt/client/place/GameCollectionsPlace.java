package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import java.util.Optional;

public class GameCollectionsPlace extends Place {

    private final String collectionId;

    public GameCollectionsPlace() {
        this(null);
    }

    public GameCollectionsPlace(final String collectionId) {
        if (collectionId == null || "null".equals(collectionId)) {
            this.collectionId = null;
        } else {
            this.collectionId = collectionId;
        }
    }

    public Optional<String> getCollectionId() {
        return Optional.ofNullable(collectionId);
    }

    @Prefix("Games")
    public static class Tokenizer implements PlaceTokenizer<GameCollectionsPlace> {

        @Override
        public String getToken(final GameCollectionsPlace place) {
            return place.collectionId;
        }

        @Override
        public GameCollectionsPlace getPlace(final String token) {
            return new GameCollectionsPlace(token);
        }

    }

}
