package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;

import java.util.Optional;

public class NormalMove extends ShogiMove implements ToSquareMove {

    protected final Piece piece;
    protected final Square fromSquare;
    protected final Square toSquare;
    protected final Piece promotionPiece;

    public NormalMove(Piece piece, Square fromSquare, Square toSquare) {
        this(piece, fromSquare, toSquare, null);
    }

    public NormalMove(Piece piece, Square fromSquare, Square toSquare, Piece promotionPiece) {
        super(piece.getOwner());
        this.piece = piece;
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.promotionPiece = promotionPiece;
    }

    public NormalMove(Piece piece, Square fromSquare, Square toSquare, boolean promote) {
        this(piece, fromSquare, toSquare, promote ? piece.getPromotedPiece() : null);
    }

    public NormalMove withPromotionPiece(Piece promotionPiece) {
        return new NormalMove(piece, fromSquare, toSquare, promotionPiece);
    }

    public Piece getPiece() {
        return piece;
    }

    public Square getFromSquare() {
        return fromSquare;
    }

    @Override
    public Square getToSquare() {
        return toSquare;
    }

    public boolean isPromote() {
        return promotionPiece != null;
    }

    public Optional<Piece> getPromotionPiece() {
        return Optional.ofNullable(promotionPiece);
    }

}
