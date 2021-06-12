package com.playshogi.library.shogi.models.record;

import java.util.ArrayList;
import java.util.List;

public class KifuCollection {

    private final List<GameRecord> kifus;
    private final String name;

    public KifuCollection(final String name, final List<GameRecord> kifus) {
        this.kifus = new ArrayList<>(kifus);
        this.name = name;
    }

    public synchronized List<GameRecord> getKifus() {
        return new ArrayList<>(kifus);
    }

    public String getName() {
        return name;
    }

    public synchronized void merge(final KifuCollection collection) {
        kifus.addAll(collection.getKifus());
    }
}
