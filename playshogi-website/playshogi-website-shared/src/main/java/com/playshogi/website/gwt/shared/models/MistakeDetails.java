package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class MistakeDetails implements Serializable {
    public enum Type {
        BLUNDER, MISTAKE, IMPRECISION, NOT_A_MISTAKE
    }

    private int moveCount;
    private String positionSfen;
    private String movePlayed;
    private String computerMove;
    private PositionScoreDetails scoreBeforeMove;
    private PositionScoreDetails scoreAfterMove;
    private Type type;

    public MistakeDetails() {
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(final int moveCount) {
        this.moveCount = moveCount;
    }

    public String getPositionSfen() {
        return positionSfen;
    }

    public void setPositionSfen(final String positionSfen) {
        this.positionSfen = positionSfen;
    }

    public String getMovePlayed() {
        return movePlayed;
    }

    public void setMovePlayed(final String movePlayed) {
        this.movePlayed = movePlayed;
    }

    public String getComputerMove() {
        return computerMove;
    }

    public void setComputerMove(final String computerMove) {
        this.computerMove = computerMove;
    }

    public PositionScoreDetails getScoreBeforeMove() {
        return scoreBeforeMove;
    }

    public void setScoreBeforeMove(final PositionScoreDetails scoreBeforeMove) {
        this.scoreBeforeMove = scoreBeforeMove;
    }

    public PositionScoreDetails getScoreAfterMove() {
        return scoreAfterMove;
    }

    public void setScoreAfterMove(final PositionScoreDetails scoreAfterMove) {
        this.scoreAfterMove = scoreAfterMove;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MistakeDetails{" +
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
