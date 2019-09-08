package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class PositionMoveDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    private String move;
    private int total;
    private int sente_wins;
    private int gote_wins;
    private String newSfen;

    public PositionMoveDetails() {
    }

    public PositionMoveDetails(final String move, final int total, final int sente_wins, final int gote_wins,
                               final String newSfen) {
        this.move = move;
        this.total = total;
        this.sente_wins = sente_wins;
        this.gote_wins = gote_wins;
        this.newSfen = newSfen;
    }

    public String getMove() {
        return move;
    }

    public int getTotal() {
        return total;
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

    public void setTotal(final int total) {
        this.total = total;
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

    @Override
    public String toString() {
        return "PositionMoveDetails [move=" + move + ", total=" + total + ", sente_wins=" + sente_wins + ", gote_wins" +
                "=" + gote_wins + "]";
    }

}
