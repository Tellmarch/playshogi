package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.position.Square;

public class DropMove extends ShogiMove implements ToSquareMove {

    private final PieceType piece;
    private final Square toSquare;

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
