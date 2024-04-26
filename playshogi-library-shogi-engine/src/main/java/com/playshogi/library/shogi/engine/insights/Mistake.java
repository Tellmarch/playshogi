package com.playshogi.library.shogi.engine.insights;

import com.playshogi.library.shogi.engine.PositionEvaluation;
import com.playshogi.library.shogi.models.position.PositionScore;

public class Mistake {
    public enum Type {
        BLUNDER, MISTAKE, IMPRECISION, NOT_A_MISTAKE
    }

    private final int moveCount;
    private final String positionSfen;
    private final String movePlayed;
    private final String computerMove;
    private final PositionScore scoreBeforeMove;
    private final PositionScore scoreAfterMove;
    private final PositionEvaluation positionEvaluation;
    private final String previousMove;
    private final Type type;

    public Mistake(final int moveCount, final String positionSfen, final String movePlayed, final String computerMove,
                   final PositionScore scoreBeforeMove, final PositionScore scoreAfterMove,
                   final PositionEvaluation positionEvaluation, final String previousMove) {
        this.moveCount = moveCount;
        this.positionSfen = positionSfen;
        this.movePlayed = movePlayed;
        this.computerMove = computerMove;
        this.scoreBeforeMove = scoreBeforeMove;
        this.scoreAfterMove = scoreAfterMove;
        this.positionEvaluation = positionEvaluation;
        this.previousMove = previousMove;
        this.type = computeType();
    }

    private Type computeType() {
        int delta = scoreBeforeMove.getEvaluationCP() + scoreAfterMove.getEvaluationCP();
        if (delta < 200) {
            return Type.NOT_A_MISTAKE;
        } else if (delta < 400 || delta < 800 && notImpactingBigPicture() || notChangingWinningSide()) {
            return Type.IMPRECISION;
        } else if (delta < 800 || notImpactingBigPicture()) {
            return Type.MISTAKE;
        } else {
            return Type.BLUNDER;
        }
    }

    private boolean notImpactingBigPicture() {
        return (scoreBeforeMove.getEvaluationCP() < -1500 && scoreAfterMove.getEvaluationCP() > 1500) ||
                (scoreBeforeMove.getEvaluationCP() > 1500 && scoreAfterMove.getEvaluationCP() < -1500);
    }

    private boolean notChangingWinningSide() {
        return (scoreBeforeMove.getEvaluationCP() < -3000 && scoreAfterMove.getEvaluationCP() > 3000) ||
                (scoreBeforeMove.getEvaluationCP() > 3000 && scoreAfterMove.getEvaluationCP() < -3000);
    }

    public int getMoveCount() {
        return moveCount;
    }

    public String getPositionSfen() {
        return positionSfen;
    }

    public String getMovePlayed() {
        return movePlayed;
    }

    public String getComputerMove() {
        return computerMove;
    }

    public PositionScore getScoreBeforeMove() {
        return scoreBeforeMove;
    }

    public PositionScore getScoreAfterMove() {
        return scoreAfterMove;
    }

    public Type getType() {
        return type;
    }

    public PositionEvaluation getPositionEvaluation() {
        return positionEvaluation;
    }

    public String getPreviousMove() {
        return previousMove;
    }

    @Override
    public String toString() {
        return "Mistake{" +
                "moveCount=" + moveCount +
                ", positionSfen='" + positionSfen + '\'' +
                ", movePlayed='" + movePlayed + '\'' +
                ", computerMove='" + computerMove + '\'' +
                ", scoreBeforeMove=" + scoreBeforeMove +
                ", scoreAfterMove=" + scoreAfterMove +
                ", type=" + type +
                '}';
    }
}
