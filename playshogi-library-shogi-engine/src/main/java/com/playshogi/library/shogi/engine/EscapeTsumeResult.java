package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.moves.ShogiMove;

public class EscapeTsumeResult {
    public enum EscapeTsumeEnum {
        TSUME,
        NOT_CHECK,
        ESCAPE,
        ESCAPE_BY_TIMEOUT
    }

    public static final EscapeTsumeResult NOT_CHECK = new EscapeTsumeResult(EscapeTsumeEnum.NOT_CHECK);

    private final EscapeTsumeEnum result;
    private final ShogiMove escapeMove;
    private final int tsumeNumMoves;
    private final String tsumeVariationUsf;

    public EscapeTsumeResult(final int tsumeNumMoves, final String tsumeVariationUsf) {
        this(EscapeTsumeEnum.TSUME, null, tsumeNumMoves, tsumeVariationUsf);
    }

    public EscapeTsumeResult(final EscapeTsumeEnum result) {
        this(result, null, 0, null);
    }

    public EscapeTsumeResult(final EscapeTsumeEnum result, final ShogiMove escapeMove) {
        this(result, escapeMove, 0, null);
    }

    private EscapeTsumeResult(final EscapeTsumeEnum result, final ShogiMove escapeMove, final int tsumeNumMoves,
                              final String tsumeVariationUsf) {
        this.result = result;
        this.escapeMove = escapeMove;
        this.tsumeNumMoves = tsumeNumMoves;
        this.tsumeVariationUsf = tsumeVariationUsf;
    }

    public EscapeTsumeEnum getResult() {
        return result;
    }

    public ShogiMove getEscapeMove() {
        return escapeMove;
    }

    public int getTsumeNumMoves() {
        return tsumeNumMoves;
    }

    public String getTsumeVariationUsf() {
        return tsumeVariationUsf;
    }

    @Override
    public String toString() {
        return "EscapeTsumeResult{" +
                "result=" + result +
                ", escapeMove=" + escapeMove +
                ", tsumeNumMoves=" + tsumeNumMoves +
                ", tsumeVariationUsf='" + tsumeVariationUsf + '\'' +
                '}';
    }
}
