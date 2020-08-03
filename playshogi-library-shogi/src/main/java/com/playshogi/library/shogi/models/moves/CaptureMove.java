package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;

public class CaptureMove extends NormalMove {

    private final Piece capturedPiece;

    public CaptureMove(Piece piece, Square fromSquare, Square toSquare, Piece capturedPiece) {
        super(piece, fromSquare, toSquare);
        this.capturedPiece = capturedPiece;
    }

    public CaptureMove(Piece piece, Square fromSquare, Square toSquare, Piece capturedPiece,
                       Piece promotionPiece) {
        super(piece, fromSquare, toSquare, promotionPiece);
        this.capturedPiece = capturedPiece;
    }

    @Deprecated
    public CaptureMove(Piece piece, Square fromSquare, Square toSquare, Piece capturedPiece, boolean promote) {
        this(piece, fromSquare, toSquare, capturedPiece, promote ? piece.getPromotedPiece() : null);
    }

    @Override
    public CaptureMove withPromotionPiece(Piece promotionPiece) {
        return new CaptureMove(piece, fromSquare, toSquare, capturedPiece, promotionPiece);
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

}
