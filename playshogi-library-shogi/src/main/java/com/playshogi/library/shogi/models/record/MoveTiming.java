package com.playshogi.library.shogi.models.record;

public class MoveTiming {
    private final int moveTimeSeconds;
    private final int totalTimeSeconds;

    public MoveTiming() {
        this(0, 0);
    }

    public MoveTiming(final int moveTimeSeconds, final int totalTimeSeconds) {
        this.moveTimeSeconds = moveTimeSeconds;
        this.totalTimeSeconds = totalTimeSeconds;
    }

    public int getMoveTimeSeconds() {
        return moveTimeSeconds;
    }

    public int getTotalTimeSeconds() {
        return totalTimeSeconds;
    }

    public String toKifString() {
        return "(" + (moveTimeSeconds / 60) + ":" + (moveTimeSeconds % 60) + "/"
                + (totalTimeSeconds / 3600) + ":" + (totalTimeSeconds / 60) + ":" + (totalTimeSeconds % 60) + ")";
    }

    @Override
    public String toString() {
        return "MoveTiming{" +
                "moveTimeSeconds=" + moveTimeSeconds +
                ", totalTimeSeconds=" + totalTimeSeconds +
                '}';
    }
}
