package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.PieceType;

public class DropMove extends ShogiMove implements ToSquareMove {

    private final PieceType piece;
    private final Square toSquare;

    public DropMove(final boolean senteMoving, final PieceType piece, final Square toSquare) {
        super(senteMoving);
        this.piece = piece;
        this.toSquare = toSquare;
    }

    public PieceType getPieceType() {
        return piece;
    }

    @Override
    public Square getToSquare() {
        return toSquare;
    }

}
