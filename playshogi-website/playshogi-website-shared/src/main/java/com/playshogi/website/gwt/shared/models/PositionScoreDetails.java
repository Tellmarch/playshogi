package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class PositionScoreDetails implements Serializable {

    public static final int MAX_SCORE = 10000;
    public static final int MIN_SCORE = -10000;

    private boolean forcedMate;
    private int numMovesBeforeMate;
    private int evaluationCP;

    public PositionScoreDetails() {
    }

    public boolean isForcedMate() {
        return forcedMate;
    }

    public void setForcedMate(final boolean forcedMate) {
        this.forcedMate = forcedMate;
    }

    public int getNumMovesBeforeMate() {
        return numMovesBeforeMate;
    }

    public void setNumMovesBeforeMate(final int numMovesBeforeMate) {
        this.numMovesBeforeMate = numMovesBeforeMate;
    }

    public int getEvaluationCP() {
        if (forcedMate) {
            return numMovesBeforeMate < 0 ? MIN_SCORE : MAX_SCORE;
        }
        return evaluationCP;
    }

    public void setEvaluationCP(final int evaluationCP) {
        this.evaluationCP = evaluationCP;
    }

    @Override
    public String toString() {
        if (forcedMate) {
            if (numMovesBeforeMate > 0) {
                return "Mate in " + numMovesBeforeMate;
            } else {
                return "Mated in " + (-numMovesBeforeMate);
            }
        } else {
            return String.valueOf(evaluationCP);
        }
    }
}
