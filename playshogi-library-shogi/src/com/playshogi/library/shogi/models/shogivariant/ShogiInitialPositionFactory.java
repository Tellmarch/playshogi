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
		shogiBoardState.setPieceAt(new Square(1, 1), Piece.GOTE_LANCE);
		shogiBoardState.setPieceAt(new Square(2, 1), Piece.GOTE_KNIGHT);
		shogiBoardState.setPieceAt(new Square(3, 1), Piece.GOTE_SILVER);
		shogiBoardState.setPieceAt(new Square(4, 1), Piece.GOTE_GOLD);
		shogiBoardState.setPieceAt(new Square(5, 1), Piece.GOTE_KING);
		shogiBoardState.setPieceAt(new Square(6, 1), Piece.GOTE_GOLD);
		shogiBoardState.setPieceAt(new Square(7, 1), Piece.GOTE_SILVER);
		shogiBoardState.setPieceAt(new Square(8, 1), Piece.GOTE_KNIGHT);
		shogiBoardState.setPieceAt(new Square(9, 1), Piece.GOTE_LANCE);

		shogiBoardState.setPieceAt(new Square(2, 2), Piece.GOTE_BISHOP);
		shogiBoardState.setPieceAt(new Square(8, 2), Piece.GOTE_ROOK);

		for (int i = 1; i <= 9; i++) {
			shogiBoardState.setPieceAt(new Square(i, 3), Piece.GOTE_PAWN);
		}

		shogiBoardState.setPieceAt(new Square(1, 9), Piece.SENTE_LANCE);
		shogiBoardState.setPieceAt(new Square(2, 9), Piece.SENTE_KNIGHT);
		shogiBoardState.setPieceAt(new Square(3, 9), Piece.SENTE_SILVER);
		shogiBoardState.setPieceAt(new Square(4, 9), Piece.SENTE_GOLD);
		shogiBoardState.setPieceAt(new Square(5, 9), Piece.SENTE_KING);
		shogiBoardState.setPieceAt(new Square(6, 9), Piece.SENTE_GOLD);
		shogiBoardState.setPieceAt(new Square(7, 9), Piece.SENTE_SILVER);
		shogiBoardState.setPieceAt(new Square(8, 9), Piece.SENTE_KNIGHT);
		shogiBoardState.setPieceAt(new Square(9, 9), Piece.SENTE_LANCE);

		shogiBoardState.setPieceAt(new Square(2, 8), Piece.SENTE_BISHOP);
		shogiBoardState.setPieceAt(new Square(8, 8), Piece.SENTE_ROOK);

		for (int i = 1; i <= 9; i++) {
			shogiBoardState.setPieceAt(new Square(i, 7), Piece.SENTE_PAWN);
		}

		return shogiPosition;
	}

}
