package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.position.PositionScore;

public class Variation {

    private PositionScore score;
    private int depth;
    private int seldepth;
    private long nodes;
    private String usf; // Variation moves in USF notation
    private int timeMs;

    public Variation() {
    }

    public PositionScore getScore() {
        return score;
    }

    public void setScore(final PositionScore score) {
        this.score = score;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getSeldepth() {
        return seldepth;
    }

    public void setSeldepth(int seldepth) {
        this.seldepth = seldepth;
    }

    public long getNodes() {
        return nodes;
    }

    public void setNodes(long nodes) {
        this.nodes = nodes;
    }

    public String getUsf() {
        return usf;
    }

    public void setUsf(String usf) {
        this.usf = usf;
    }

    public int getTimeMs() {
        return timeMs;
    }

    public void setTimeMs(int timeMs) {
        this.timeMs = timeMs;
    }

    @Override
    public String toString() {
        return "Variation{" +
                "score=" + score +
                ", depth=" + depth +
                ", seldepth=" + seldepth +
                ", nodes=" + nodes +
                ", usf='" + usf + '\'' +
                ", timeMs=" + timeMs +
                '}';
    }
}
