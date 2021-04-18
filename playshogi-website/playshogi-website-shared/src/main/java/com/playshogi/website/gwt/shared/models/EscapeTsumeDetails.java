package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class EscapeTsumeDetails implements Serializable {
    public enum ResultEnum {
        TSUME,
        NOT_CHECK,
        ESCAPE
    }

    private ResultEnum result;
    private String escapeMove;

    public EscapeTsumeDetails() {
    }

    public EscapeTsumeDetails(final ResultEnum result, final String escapeMove) {
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

    @Override
    public String toString() {
        return "EscapeTsumeResult{" +
                "result=" + result +
                ", escapeMove='" + escapeMove + '\'' +
                '}';
    }
}
