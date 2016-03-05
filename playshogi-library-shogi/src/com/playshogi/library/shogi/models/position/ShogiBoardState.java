package com.playshogi.library.shogi.models.position;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.formats.usf.UsfUtil;

public abstract class ShogiBoardState {

	public static final int FIRST_ROW = 1;
	public static final int FIRST_COLUMN = 1;

	public abstract Piece getPieceAt(int column, int row);

	public abstract Piece getPieceAt(Square square);

	public abstract void setPieceAt(int column, int row, Piece piece);

	public abstract void setPieceAt(Square square, Piece piece);

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract ShogiBoardState opposite();

	public abstract boolean hasPlayerPawnOnColumn(boolean isPlayerSente, int column);

	public abstract boolean isSquareEmptyOrGote(Square square);

	public int getLastRow() {
		return getHeight();
	}

	public int getLastColumn() {
		return getWidth();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(600);
		for (int row = FIRST_ROW; row <= getLastRow(); row++) {
			builder.append("----------------------------\n");
			for (int column = FIRST_COLUMN; column <= getLastColumn(); column++) {
				builder.append('|');
				Piece pieceAt = getPieceAt(10 - column, row);
				if (pieceAt == null) {
					builder.append("  ");
				} else {
					String pieceToString = UsfUtil.pieceToString(pieceAt);
					if (pieceToString.length() == 1) {
						builder.append(' ');
					}
					builder.append(pieceToString);
				}
			}
			builder.append("|\n");
		}

		builder.append("----------------------------\n");

		return builder.toString();
	}
}