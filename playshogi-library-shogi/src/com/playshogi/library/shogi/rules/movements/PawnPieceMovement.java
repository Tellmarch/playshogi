package com.playshogi.library.shogi.rules.movements;

import java.util.Collections;
import java.util.List;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

public class PawnPieceMovement implements PieceMovement {

	@Override
	public List<Square> getPossibleMoves(final ShogiBoardState position, final Square from) {
		if (from.getRow() != 0) {
			Piece piece = position.getPieceAt(from.getColumn(), from.getRow() - 1);
			if (piece == null || !piece.isSentePiece()) {
				return Collections.singletonList(Square.of(from.getColumn(), from.getRow() - 1));
			}
		}
		return Collections.emptyList();

	}

	@Override
	public boolean isMoveValid(final ShogiBoardState position, final Square from, final Square to) {
		return from.getColumn() == to.getColumn() && from.getRow() == to.getRow() + 1;
	}

	@Override
	public boolean isDropValid(final ShogiBoardState position, final Square to) {
		return !position.hasSentePawnOnColumn(to.getColumn()) && !checkMatePawnMove(position, to);
	}

	private boolean checkMatePawnMove(final ShogiBoardState position, final Square to) {
		// TODO Auto-generated method stub
		return false;
	}

}
