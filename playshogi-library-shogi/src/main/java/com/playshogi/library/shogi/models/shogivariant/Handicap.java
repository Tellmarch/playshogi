package com.playshogi.library.shogi.models.shogivariant;

public enum Handicap {
    EVEN("平手"),
    SENTE(""),
    LANCE("香落ち"),
    BISHOP("角落ち"),
    ROOK("飛車落ち"),
    ROOK_LANCE("飛香落ち"),
    TWO_PIECES("二枚落ち"),
    FOUR_PIECES("四枚落ち"),
    SIX_PIECES("六枚落ち"),
    EIGHT_PIECES("八枚落ち"),
    NINE_PIECES(""),
    TEN_PIECES("十枚落ち"),
    THREE_PAWNS(""),
    NAKED_KING("");

    private final String japanese;

    Handicap(final String japanese) {
        this.japanese = japanese;
    }

    public String getJapanese() {
        return japanese;
    }
}
