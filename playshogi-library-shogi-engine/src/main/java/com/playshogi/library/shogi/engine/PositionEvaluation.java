package com.playshogi.library.shogi.engine;

import java.util.Arrays;

public class PositionEvaluation {

    private final String sfen;
    private final PrincipalVariation[] principalVariationHistory;
    private final String bestMove;
    private final String ponderMove;

    public PositionEvaluation(final String sfen, final PrincipalVariation[] principalVariationHistory,
                              final String bestMove, final String ponderMove) {
        this.sfen = sfen;
        this.principalVariationHistory = principalVariationHistory;
        this.bestMove = bestMove;
        this.ponderMove = ponderMove;
    }

    public String getSfen() {
        return sfen;
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
                "sfen='" + sfen + '\'' +
                ", principalVariationHistory=" + Arrays.toString(principalVariationHistory) +
                ", bestMove='" + bestMove + '\'' +
                ", ponderMove='" + ponderMove + '\'' +
                '}';
    }
}
