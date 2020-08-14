package com.playshogi.library.shogi.models.position;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;

import java.util.Optional;

class InvertedShogiBoardState extends ShogiBoardState {
    private final ShogiBoardState original;

    public InvertedShogiBoardState(final ShogiBoardState original) {
        this.original = original;
    }

    @Override
    public Optional<Piece> getPieceAt(final int column, final int row) {
        return getPieceAt(Square.of(column, row));
    }

    @Override
    public Optional<Piece> getPieceAt(final Square square) {
        Optional<Piece> piece = original.getPieceAt(square.opposite());
        return piece.map(Piece::opposite);
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
        Optional<Piece> piece = original.getPieceAt(square.opposite());
        return !piece.isPresent() || piece.get().isSentePiece();
    }
}
