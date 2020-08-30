package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.moves.ShogiMove;

public class EscapeTsumeResult {
    public enum EscapeTsumeEnum {
        TSUME,
        NOT_CHECK,
        ESCAPE
    }

    public static final EscapeTsumeResult NOT_CHECK = new EscapeTsumeResult(EscapeTsumeEnum.NOT_CHECK);

    private final EscapeTsumeEnum result;
    private final ShogiMove escapeMove;

    public EscapeTsumeResult(EscapeTsumeEnum result) {
        this(result, null);
    }

    public EscapeTsumeResult(EscapeTsumeEnum result, ShogiMove escapeMove) {
        this.result = result;
        this.escapeMove = escapeMove;
    }

    public EscapeTsumeEnum getResult() {
        return result;
    }

    public ShogiMove getEscapeMove() {
        return escapeMove;
    }

    @Override
    public String toString() {
        return "EscapeTsumeResult{" +
                "result=" + result +
                ", escapeMove=" + escapeMove +
                '}';
    }

    public String toPrettyString() {
        switch (result) {
            case TSUME:
                return "The position is Tsume!";
            case NOT_CHECK:
                return "Gote is not in check!";
            case ESCAPE:
                return "Gote can escape with the move " + escapeMove + "!";
            default:
                throw new IllegalArgumentException("Unknown type: " + result);
        }
    }

}
