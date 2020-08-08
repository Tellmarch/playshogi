package com.playshogi.library.shogi.engine;

import java.util.Arrays;

public class PositionEvaluation {

    private final PrincipalVariation[] principalVariationHistory;
    private final String bestMove;
    private final String ponderMove;

    public PositionEvaluation(PrincipalVariation[] principalVariationHistory, String bestMove, String ponderMove) {
        this.principalVariationHistory = principalVariationHistory;
        this.bestMove = bestMove;
        this.ponderMove = ponderMove;
    }

    public PrincipalVariation[] getPrincipalVariationHistory() {
        return principalVariationHistory;
    }

    public String getBestMove() {
        return bestMove;
    }

    public String getPonderMove() {
        return ponderMove;
    }

    @Override
    public String toString() {
        return "PositionEvaluation{" +
                "principalVariationHistory=" + Arrays.toString(principalVariationHistory) +
                ", bestMove='" + bestMove + '\'' +
                ", ponderMove='" + ponderMove + '\'' +
                '}';
    }
}
