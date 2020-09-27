package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class GameCollectionDetailsAndGames implements Serializable {
    private GameCollectionDetails details;
    private KifuDetails[] games;

    public GameCollectionDetailsAndGames() {
    }

    public GameCollectionDetailsAndGames(final GameCollectionDetails details, final KifuDetails[] games) {
        this.details = details;
        this.games = games;
    }

    public GameCollectionDetails getDetails() {
        return details;
    }

    public void setDetails(final GameCollectionDetails details) {
        this.details = details;
    }

    public KifuDetails[] getGames() {
        return games;
    }

    public void setGames(final KifuDetails[] games) {
        this.games = games;
    }

    @Override
    public String toString() {
        return "GameCollectionDetailsAndGames{" +
                "details=" + details +
                ", games=" + Arrays.toString(games) +
                '}';
    }
}
