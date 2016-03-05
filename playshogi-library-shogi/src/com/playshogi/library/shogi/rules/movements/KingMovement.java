package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

public class KingMovement extends AbstractPieceMovement {
	private static final int[][] KING_ALLOWED_DCOL_DROW = { { +1, -1 }, { +1, 0 }, { 0, -1 }, { 0, +1 }, { -1, -1 },
			{ -1, 0 }, { +1, +1 }, { -1, +1 } };

	public KingMovement() {
		super(KING_ALLOWED_DCOL_DROW);
	}

	@Override
	public boolean isDropValid(final ShogiBoardState boardState, final Square to) {
		return false;
	}
}
