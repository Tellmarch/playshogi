package com.playshogi.library.shogi.models.position;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvertedShogiPosition implements ReadOnlyShogiPosition {

    private final ReadOnlyShogiPosition originalPosition;

    InvertedShogiPosition(ReadOnlyShogiPosition position) {
        originalPosition = position;
    }

    @Override
    public int getMoveCount() {
        return originalPosition.getMoveCount();
    }

    @Override
    public Player getPlayerToMove() {
        return originalPosition.getPlayerToMove().opposite();
    }

    @Override
    public ShogiBoardState getShogiBoardState() {
        return originalPosition.getShogiBoardState().opposite();
    }

    @Override
    public int getColumns() {
        return originalPosition.getColumns();
    }

    @Override
    public int getRows() {
        return originalPosition.getRows();
    }

    @Override
    public KomadaiState getSenteKomadai() {
        return originalPosition.getGoteKomadai();
    }

    @Override
    public KomadaiState getGoteKomadai() {
        return originalPosition.getSenteKomadai();
    }

    @Override
    public Optional<Piece> getPieceAt(final Square square) {
        return getShogiBoardState().getPieceAt(square);
    }

    @Override
    public boolean isEmptySquare(final Square square) {
        return !getShogiBoardState().getPieceAt(square).isPresent();
    }

    @Override
    public boolean hasBlackPieceAt(final Square square) {
        return getShogiBoardState().getPieceAt(square).isPresent() && getShogiBoardState().getPieceAt(square).get().isBlackPiece();
    }

    @Override
    public boolean hasWhitePieceAt(final Square square) {
        return getShogiBoardState().getPieceAt(square).isPresent() && getShogiBoardState().getPieceAt(square).get().isWhitePiece();
    }

    @Override
    public boolean hasSenteKingOnBoard() {
        for (int i = 1; i <= getRows(); i++) {
            for (int j = 1; j <= getColumns(); j++) {
                if (getShogiBoardState().getPieceAt(i, j).orElse(null) == Piece.GOTE_KING) return true;
            }
        }
        return false;
    }

    @Override
    public List<Square> getAllSquares() {
        List<Square> squares = new ArrayList<>();
        for (int row = 1; row <= getShogiBoardState().getLastRow(); row++) {
            for (int column = 1; column <= getShogiBoardState().getLastColumn(); column++) {
                squares.add(Square.of(column, row));
            }
        }
        return squares;
    }

    @Override
    public ShogiPosition clonePosition() {
        // TODO optimize... maybe...
        return SfenConverter.fromSFEN(SfenConverter.toSFEN(this));
    }

    @Override
    public ReadOnlyShogiPosition opposite() {
        return originalPosition;
    }

    @Override
    public String toString() {
        return getPlayerToMove().name() + " to move:\n" + getShogiBoardState().toString();
    }

    @Override
    public boolean isDefaultStartingPosition() {
        return ShogiInitialPositionFactory.READ_ONLY_INITIAL_POSITION.equals(this);
    }
}
