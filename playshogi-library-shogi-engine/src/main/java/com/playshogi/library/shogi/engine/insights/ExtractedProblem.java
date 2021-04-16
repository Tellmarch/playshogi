package com.playshogi.library.shogi.engine.insights;

import com.playshogi.library.shogi.models.record.GameInformation;

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
    private final String previousMove;
    private final GameInformation gameInformation;

    public ExtractedProblem(final ProblemType type, final String sfen, final String variation,
                            final String previousMove, final GameInformation gameInformation) {
        this.type = type;
        this.sfen = sfen;
        this.variation = variation;
        this.previousMove = previousMove;
        this.gameInformation = gameInformation;
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

    public String getPreviousMove() {
        return previousMove;
    }

    public GameInformation getGameInformation() {
        return gameInformation;
    }

    @Override
    public String toString() {
        return "ExtractedProblem{" +
                "type=" + type +
                ", sfen='" + sfen + '\'' +
                ", variation='" + variation + '\'' +
                ", previousMove='" + previousMove + '\'' +
                ", gameInformation=" + gameInformation +
                '}';
    }
}
