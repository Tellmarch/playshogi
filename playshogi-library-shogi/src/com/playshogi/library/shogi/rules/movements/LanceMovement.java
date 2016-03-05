package com.playshogi.library.shogi.rules.movements;

import java.util.ArrayList;
import java.util.List;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

public class LanceMovement implements PieceMovement {

	@Override
	public List<Square> getPossibleMoves(final ShogiBoardState boardState, final Square from) {
		List<Square> result = new ArrayList<Square>();
		return null;
	}

	@Override
	public boolean isMoveValid(final ShogiBoardState boardState, final Square from, final Square to) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDropValid(final ShogiBoardState boardState, final Square to) {
		// TODO Auto-generated method stub
		return false;
	}

}
