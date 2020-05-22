package com.playshogi.library.shogi.models.position;

import com.playshogi.library.shogi.models.PieceType;

public class KomadaiState {
    private final int[] pieces = new int[PieceType.values().length];

    public void removePiece(final PieceType piece) {
        pieces[piece.ordinal()]--;
    }

    public void addPiece(final PieceType piece) {
        pieces[piece.ordinal()]++;
    }

    public void setPiecesOfType(final PieceType piece, final int i) {
        pieces[piece.ordinal()] = i;
    }

    /**
     *
     * @return int number of pieces in hand
     */
    public int getPiecesOfType(final PieceType piece) {
        return pieces[piece.ordinal()];
    }

    /**
     *
     * @return array of number of pieces in hand (index of array = piece type)
     */
    public int[] getPieces() {
        return pieces;
    }
}
