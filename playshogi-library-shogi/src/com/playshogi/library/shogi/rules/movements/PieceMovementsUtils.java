package com.playshogi.library.shogi.rules.movements;

import java.util.List;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

public class PieceMovementsUtils {
	public static void addSquaresAlongDirection(final ShogiBoardState boardState, final Square from, final int dCol,
			final int dRow, final List<Square> result) {
		int row = from.getRow() + dRow;
		int col = from.getColumn() + dCol;
		Square square = getSquare(boardState, col, row);

		while (square != null) {
			Piece piece = boardState.getPieceAt(col, row);

			if (piece != null && piece.isSentePiece()) {
				break;
			}

			result.add(Square.of(col, row));

			if (piece != null && !piece.isSentePiece()) {
				break;
			}

			row += dRow;
			col += dCol;
			square = getSquare(boardState, col, row);
		}
	}

	public static Square getSquare(final ShogiBoardState boardState, final int column, final int row) {
		if (column >= ShogiBoardState.FIRST_COLUMN && column <= boardState.getWidth()
				&& row >= ShogiBoardState.FIRST_ROW && row <= boardState.getHeight()) {
			return Square.of(column, row);
		} else {
			return null;
		}
	}
}
