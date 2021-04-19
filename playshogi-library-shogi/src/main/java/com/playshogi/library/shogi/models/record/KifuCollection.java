package com.playshogi.library.shogi.models.record;

import java.util.List;

public class KifuCollection {

    private final List<GameRecord> kifus;
    private final String name;

    public KifuCollection(final String name, final List<GameRecord> kifus) {
        this.kifus = kifus;
        this.name = name;
    }

    public List<GameRecord> getKifus() {
        return kifus;
    }

    public String getName() {
        return name;
    }

}
