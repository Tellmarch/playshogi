package com.playshogi.library.shogi.engine.insights;

public class GameInsights {
    private final PlayerAccuracy blackAccuracy;
    private final PlayerAccuracy whiteAccuracy;

    public GameInsights(final PlayerAccuracy blackAccuracy, final PlayerAccuracy whiteAccuracy) {
        this.blackAccuracy = blackAccuracy;
        this.whiteAccuracy = whiteAccuracy;
    }

    public PlayerAccuracy getBlackAccuracy() {
        return blackAccuracy;
    }

    public PlayerAccuracy getWhiteAccuracy() {
        return whiteAccuracy;
    }

    @Override
    public String toString() {
        return "GameInsights{" +
                "blackAccuracy=" + blackAccuracy +
                ", whiteAccuracy=" + whiteAccuracy +
                '}';
    }
}
