package com.playshogi.library.shogi.models.position;

import com.playshogi.library.shogi.models.PieceType;

public interface KomadaiState {
    int getPiecesOfType(PieceType piece);
    boolean isEmpty();
    int[] getPieces();
}
