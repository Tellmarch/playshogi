package com.playshogi.library.shogi.engine.insights;

import java.util.List;

public class PlayerAccuracy {
    private final int averageCentipawnsLost;
    private final List<Mistake> mistakes;

    public PlayerAccuracy(final int averageCentipawnsLost, final List<Mistake> mistakes) {
        this.averageCentipawnsLost = averageCentipawnsLost;
        this.mistakes = mistakes;
    }

    public List<Mistake> getMistakes() {
        return mistakes;
    }

    public int getAverageCentipawnsLost() {
        return averageCentipawnsLost;
    }

    @Override
    public String toString() {
        return "PlayerAccuracy{" +
                "averageCentipawnsLost=" + averageCentipawnsLost +
                ", mistakes=" + mistakes +
                '}';
    }
}
