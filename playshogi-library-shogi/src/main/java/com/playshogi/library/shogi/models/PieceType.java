package com.playshogi.library.shogi.models;

import java.util.Arrays;
import java.util.Collections;

public enum PieceType {
    PAWN, LANCE, KNIGHT, SILVER, GOLD, BISHOP, ROOK, KING;

    public static final PieceType[] WEAKEST_TO_STRONGEST = PieceType.values();
    public static final PieceType[] STRONGEST_TO_WEAKEST = getReverseOrder();

    private static PieceType[] getReverseOrder() {
        PieceType[] values = PieceType.values();
        Collections.reverse(Arrays.asList(values));
        return values;
    }

}
