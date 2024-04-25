package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class PositionEvaluationDetails implements Serializable {

    private String sfen;
    private PrincipalVariationDetails[] topPrincipalVariations; // Index 0 is the best line
    private String bestMove;
    private String ponderMove;
    private TsumeAnalysisDetails tsumeAnalysis;

    public PositionEvaluationDetails() {
    }

    public String getSfen() {
        return sfen;
    }

    public void setSfen(final String sfen) {
        this.sfen = sfen;
    }

    public PrincipalVariationDetails[] getTopPrincipalVariations() {
        return topPrincipalVariations;
    }

    public void setTopPrincipalVariations(final PrincipalVariationDetails[] topPrincipalVariations) {
        this.topPrincipalVariations = topPrincipalVariations;
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

    public TsumeAnalysisDetails getTsumeAnalysis() {
        return tsumeAnalysis;
    }

    public void setTsumeAnalysis(TsumeAnalysisDetails tsumeAnalysis) {
        this.tsumeAnalysis = tsumeAnalysis;
    }

    @Override
    public String toString() {
        return "PositionEvaluationDetails{" +
                "sfen='" + sfen + '\'' +
                ", topPrincipalVariations=" + Arrays.toString(topPrincipalVariations) +
                ", bestMove='" + bestMove + '\'' +
                ", ponderMove='" + ponderMove + '\'' +
                ", tsumeAnalysis=" + tsumeAnalysis +
                '}';
    }
}
