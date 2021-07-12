package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class TsumeAnalysisDetails implements Serializable {
    public enum ResultEnum {
        TSUME,
        NOT_CHECK,
        ESCAPE,
        ESCAPE_BY_TIMEOUT,
        FIND_TSUME_TIMEOUT,
        NO_MATE
    }

    private ResultEnum result;
    private String escapeMove;
    private int tsumeNumMoves;

    public TsumeAnalysisDetails() {
    }

    public TsumeAnalysisDetails(final ResultEnum result, final String escapeMove) {
        this.result = result;
        this.escapeMove = escapeMove;
    }

    public ResultEnum getResult() {
        return result;
    }

    public String getEscapeMove() {
        return escapeMove;
    }

    public void setResult(final ResultEnum result) {
        this.result = result;
    }

    public void setEscapeMove(final String escapeMove) {
        this.escapeMove = escapeMove;
    }

    public int getTsumeNumMoves() {
        return tsumeNumMoves;
    }

    public void setTsumeNumMoves(final int tsumeNumMoves) {
        this.tsumeNumMoves = tsumeNumMoves;
    }

    @Override
    public String toString() {
        return "TsumeAnalysisDetails{" +
                "result=" + result +
                ", escapeMove='" + escapeMove + '\'' +
                ", tsumeNumMoves=" + tsumeNumMoves +
                '}';
    }
}
