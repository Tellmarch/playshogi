package com.playshogi.library.shogi.models.record;

import java.util.List;

public class GameCollection {

    private final List<GameRecord> games;
    private final String name;

    public GameCollection(final String name, final List<GameRecord> games) {
        this.games = games;
        this.name = name;
    }

    public List<GameRecord> getGames() {
        return games;
    }

    public String getName() {
        return name;
    }

}
