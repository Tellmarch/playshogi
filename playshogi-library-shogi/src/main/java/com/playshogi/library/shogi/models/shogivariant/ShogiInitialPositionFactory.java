package com.playshogi.library.shogi.models.shogivariant;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.position.ShogiBoardState;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class ShogiInitialPositionFactory implements InitialPositionFactory {

    public ShogiPosition createInitialPosition() {
        return createInitialPosition(Handicap.HIRATE);
    }

    @Override
    public ShogiPosition createInitialPosition(final Handicap handicap) {
        ShogiPosition shogiPosition = new ShogiPosition(ShogiVariant.NORMAL_SHOGI);
        ShogiBoardState shogiBoardState = shogiPosition.getShogiBoardState();
        shogiBoardState.setPieceAt(Square.of(1, 1), Piece.GOTE_LANCE);
        shogiBoardState.setPieceAt(Square.of(2, 1), Piece.GOTE_KNIGHT);
        shogiBoardState.setPieceAt(Square.of(3, 1), Piece.GOTE_SILVER);
        shogiBoardState.setPieceAt(Square.of(4, 1), Piece.GOTE_GOLD);
        shogiBoardState.setPieceAt(Square.of(5, 1), Piece.GOTE_KING);
        shogiBoardState.setPieceAt(Square.of(6, 1), Piece.GOTE_GOLD);
        shogiBoardState.setPieceAt(Square.of(7, 1), Piece.GOTE_SILVER);
        shogiBoardState.setPieceAt(Square.of(8, 1), Piece.GOTE_KNIGHT);
        shogiBoardState.setPieceAt(Square.of(9, 1), Piece.GOTE_LANCE);

        shogiBoardState.setPieceAt(Square.of(2, 2), Piece.GOTE_BISHOP);
        shogiBoardState.setPieceAt(Square.of(8, 2), Piece.GOTE_ROOK);

        for (int i = 1; i <= 9; i++) {
            shogiBoardState.setPieceAt(Square.of(i, 3), Piece.GOTE_PAWN);
        }

        shogiBoardState.setPieceAt(Square.of(1, 9), Piece.SENTE_LANCE);
        shogiBoardState.setPieceAt(Square.of(2, 9), Piece.SENTE_KNIGHT);
        shogiBoardState.setPieceAt(Square.of(3, 9), Piece.SENTE_SILVER);
        shogiBoardState.setPieceAt(Square.of(4, 9), Piece.SENTE_GOLD);
        shogiBoardState.setPieceAt(Square.of(5, 9), Piece.SENTE_KING);
        shogiBoardState.setPieceAt(Square.of(6, 9), Piece.SENTE_GOLD);
        shogiBoardState.setPieceAt(Square.of(7, 9), Piece.SENTE_SILVER);
        shogiBoardState.setPieceAt(Square.of(8, 9), Piece.SENTE_KNIGHT);
        shogiBoardState.setPieceAt(Square.of(9, 9), Piece.SENTE_LANCE);

        shogiBoardState.setPieceAt(Square.of(8, 8), Piece.SENTE_BISHOP);
        shogiBoardState.setPieceAt(Square.of(2, 8), Piece.SENTE_ROOK);

        for (int i = 1; i <= 9; i++) {
            shogiBoardState.setPieceAt(Square.of(i, 7), Piece.SENTE_PAWN);
        }

        return shogiPosition;
    }

}
