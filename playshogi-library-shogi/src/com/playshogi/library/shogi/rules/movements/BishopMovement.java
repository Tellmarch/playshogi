package com.playshogi.library.shogi.rules.movements;

import java.util.ArrayList;
import java.util.List;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

public class BishopMovement implements PieceMovement {

	@Override
	public List<Square> getPossibleMoves(final ShogiBoardState boardState, final Square from) {
		List<Square> result = new ArrayList<Square>();
		PieceMovementsUtils.addSquaresAlongDirection(boardState, from, -1, -1, result);
		PieceMovementsUtils.addSquaresAlongDirection(boardState, from, -1, +1, result);
		PieceMovementsUtils.addSquaresAlongDirection(boardState, from, +1, -1, result);
		PieceMovementsUtils.addSquaresAlongDirection(boardState, from, +1, +1, result);
		return result;
	}

	@Override
	public boolean isMoveDxDyValid(final ShogiBoardState boardState, final Square from, final Square to) {
		// TODO write more efficient method?
		return getPossibleMoves(boardState, from).contains(to);
	}

	@Override
	public boolean isDropValid(final ShogiBoardState boardState, final Square to) {
		return true;
	}

}
