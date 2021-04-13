package com.playshogi.library.shogi.models.position;

public class PositionScore {

    public static final int MAX_SCORE = 10000;
    public static final int MIN_SCORE = -10000;

    private final boolean forcedMate;
    private final int numMovesBeforeMate;
    private final int evaluationCP;

    public static PositionScore mateIn(int numMovesBeforeMate) {
        return new PositionScore(true, numMovesBeforeMate, 0);
    }

    public static PositionScore fromScore(int evaluationCP) {
        return new PositionScore(false, 0, evaluationCP);
    }

    private PositionScore(final boolean forcedMate, final int numMovesBeforeMate, final int evaluationCP) {
        this.forcedMate = forcedMate;
        this.numMovesBeforeMate = numMovesBeforeMate;
        this.evaluationCP = evaluationCP;
    }

    public boolean isForcedMate() {
        return forcedMate;
    }

    public int getNumMovesBeforeMate() {
        return numMovesBeforeMate;
    }

    public int getEvaluationCP() {
        if (forcedMate) {
            return numMovesBeforeMate < 0 ? MIN_SCORE : MAX_SCORE;
        }
        return evaluationCP;
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
