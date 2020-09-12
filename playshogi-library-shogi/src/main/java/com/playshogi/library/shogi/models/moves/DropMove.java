package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;

public class DropMove extends ShogiMove implements ToSquareMove {

    private final PieceType piece;
    private final Square toSquare;

    @Deprecated
    public DropMove(final boolean senteMoving, final PieceType piece, final Square toSquare) {
        this(senteMoving ? Player.BLACK : Player.WHITE, piece, toSquare);
    }

    public DropMove(final Player player, final PieceType piece, final Square toSquare) {
        super(player);
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
