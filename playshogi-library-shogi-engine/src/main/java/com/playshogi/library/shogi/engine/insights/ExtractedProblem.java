package com.playshogi.library.shogi.engine.insights;

public class ExtractedProblem {
    public enum ProblemType {
        MATE_OR_LOSING,
        MATE_OR_BE_MATED,
        WINNING_OR_LOSING,
        WINNING_OR_BE_MATED,
        ESCAPE_MATE
    }

    private final ProblemType type;
    private final String sfen;
    private final String variation;

    public ExtractedProblem(final ProblemType type, final String sfen, final String variation) {
        this.type = type;
        this.sfen = sfen;
        this.variation = variation;
    }

    public ProblemType getType() {
        return type;
    }

    public String getSfen() {
        return sfen;
    }

    public String getVariation() {
        return variation;
    }

    @Override
    public String toString() {
        return "ExtractedProblem{" +
                "type=" + type +
                ", sfen='" + sfen + '\'' +
                ", variation='" + variation + '\'' +
                '}';
    }
}
