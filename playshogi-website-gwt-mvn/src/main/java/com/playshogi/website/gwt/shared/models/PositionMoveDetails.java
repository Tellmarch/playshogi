package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class PositionMoveDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    private String move;
    private int moveOcurrences;
    private int positionOccurences;
    private int sente_wins;
    private int gote_wins;
    private String newSfen;

    public PositionMoveDetails() {
    }

    public PositionMoveDetails(final String move, final int moveOcurrences, final int positionOccurences,
                               final int sente_wins, final int gote_wins,
                               final String newSfen) {
        this.move = move;
        this.moveOcurrences = moveOcurrences;
        this.positionOccurences = positionOccurences;
        this.sente_wins = sente_wins;
        this.gote_wins = gote_wins;
        this.newSfen = newSfen;
    }

    public String getMove() {
        return move;
    }

    public int getMoveOcurrences() {
        return moveOcurrences;
    }

    public int getSente_wins() {
        return sente_wins;
    }

    public int getGote_wins() {
        return gote_wins;
    }

    public void setMove(final String move) {
        this.move = move;
    }

    public void setMoveOcurrences(final int moveOcurrences) {
        this.moveOcurrences = moveOcurrences;
    }

    public void setSente_wins(final int sente_wins) {
        this.sente_wins = sente_wins;
    }

    public void setGote_wins(final int gote_wins) {
        this.gote_wins = gote_wins;
    }

    public String getNewSfen() {
        return newSfen;
    }

    public int getPositionOccurences() {
        return positionOccurences;
    }

    public void setPositionOccurences(int positionOccurences) {
        this.positionOccurences = positionOccurences;
    }

    public void setNewSfen(String newSfen) {
        this.newSfen = newSfen;
    }

    @Override
    public String toString() {
        return "PositionMoveDetails{" +
                "move='" + move + '\'' +
                ", moveOcurrences=" + moveOcurrences +
                ", positionOccurences=" + positionOccurences +
                ", sente_wins=" + sente_wins +
                ", gote_wins=" + gote_wins +
                ", newSfen='" + newSfen + '\'' +
                '}';
    }
}
