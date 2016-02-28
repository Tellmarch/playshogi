package com.playshogi.library.shogi.models.shogivariant;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.position.ShogiBoardState;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class ShogiInitialPositionFactory implements InitialPositionFactory {

	@Override
	public ShogiPosition createInitialPosition(final Handicap handicap) {
		ShogiPosition shogiPosition = new ShogiPosition(ShogiVariant.NORMAL_SHOGI);
		ShogiBoardState shogiBoardState = shogiPosition.getShogiBoardState();
		shogiBoardState.setPieceAt(new Square(1, 1), new Piece(false, PieceType.LANCE, false));

		return shogiPosition;
	}

}
