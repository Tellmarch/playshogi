package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class PositionEvaluationDetails implements Serializable {

    private PrincipalVariationDetails[] principalVariationHistory;
    private String bestMove;
    private String ponderMove;

    public PositionEvaluationDetails() {
    }

    public PrincipalVariationDetails[] getPrincipalVariationHistory() {
        return principalVariationHistory;
    }

    public void setPrincipalVariationHistory(PrincipalVariationDetails[] principalVariationHistory) {
        this.principalVariationHistory = principalVariationHistory;
    }

    public String getBestMove() {
        return bestMove;
    }

    public void setBestMove(String bestMove) {
        this.bestMove = bestMove;
    }

    public String getPonderMove() {
        return ponderMove;
    }

    public void setPonderMove(String ponderMove) {
        this.ponderMove = ponderMove;
    }

    @Override
    public String toString() {
        return "PositionEvaluationDetails{" +
                "principalVariationHistory=" + Arrays.toString(principalVariationHistory) +
                ", bestMove='" + bestMove + '\'' +
                ", ponderMove='" + ponderMove + '\'' +
                '}';
    }
}
