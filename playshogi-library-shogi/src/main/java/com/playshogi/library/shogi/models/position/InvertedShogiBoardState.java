package com.playshogi.library.shogi.models.position;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;

class InvertedShogiBoardState extends ShogiBoardState {
    private final ShogiBoardState original;

    public InvertedShogiBoardState(final ShogiBoardState original) {
        this.original = original;
    }

    @Override
    public Piece getPieceAt(final int column, final int row) {
        return getPieceAt(Square.of(column, row));
    }

    @Override
    public Piece getPieceAt(final Square square) {
        return Piece.getOppositePiece(original.getPieceAt(square.opposite()));
    }

    @Override
    public void setPieceAt(final int column, final int row, final Piece piece) {
        setPieceAt(Square.of(column, row), piece);
    }

    @Override
    public void setPieceAt(final Square square, final Piece piece) {
        original.setPieceAt(square.opposite(), Piece.getOppositePiece(piece.opposite()));
    }

    @Override
    public int getWidth() {
        return original.getWidth();
    }

    @Override
    public int getHeight() {
        return original.getHeight();
    }

    @Override
    public boolean hasPlayerPawnOnColumn(final boolean isPlayerSente, final int column) {
        return original.hasPlayerPawnOnColumn(!isPlayerSente, 10 - column);
    }

    @Override
    public ShogiBoardState opposite() {
        return original;
    }

    @Override
    public boolean isSquareEmptyOrGote(final Square square) {
        Piece piece = original.getPieceAt(square.opposite());
        return piece == null || piece.isSentePiece();
    }
}