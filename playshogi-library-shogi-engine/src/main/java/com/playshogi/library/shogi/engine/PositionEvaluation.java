package com.playshogi.library.shogi.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PositionEvaluation {

    private final String sfen;
    private final List<MultiVariations> variationsHistory; // Index 0 is oldest evaluation (most imprecise)
    // All moves in USF notation
    private final String bestMove;
    private final String ponderMove;

    public PositionEvaluation(final String sfen, final List<MultiVariations> variationsHistory,
                              final String bestMove, final String ponderMove) {
        this.sfen = sfen;
        this.variationsHistory = variationsHistory;
        this.bestMove = bestMove;
        this.ponderMove = ponderMove;
    }

    public String getSfen() {
        return sfen;
    }

    public List<MultiVariations> getVariationsHistory() {
        return new ArrayList<>(variationsHistory);
    }

    public List<Variation> getPrincipalVariationsHistory() {
        return variationsHistory.stream().map(MultiVariations::getMainVariation).collect(Collectors.toList());
    }

    public Variation getMainVariation() {
        return getMultiVariations().getMainVariation();
    }

    public MultiVariations getMultiVariations() {
        return variationsHistory.get(variationsHistory.size() - 1);
    }

    public String getBestMove() {
        return bestMove;
    }

    public String getPonderMove() {
        return ponderMove;
    }

    public PositionScore getScore() {
        return getMainVariation().getScore();
    }

    @Override
    public String toString() {
        return "PositionEvaluation{" +
                "sfen='" + sfen + '\'' +
                ", score=" + getScore() +
                ", variationsHistory=" + variationsHistory +
                ", bestMove='" + bestMove + '\'' +
                ", ponderMove='" + ponderMove + '\'' +
                '}';
    }
}
