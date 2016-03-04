package com.playshogi.library.shogi.models.position;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;

public class ShogiBoardState {

	private final int width;
	private final int height;

	private final Piece[][] board;

	public ShogiBoardState(final int width, final int height) {
		this.width = width;
		this.height = height;

		board = new Piece[width][height];
	}

	public Piece getPieceAt(final int column, final int row) {
		return board[column - 1][row - 1];
	}

	public void setPieceAt(final int column, final int row, final Piece piece) {
		board[column - 1][row - 1] = piece;
	}

	public void setPieceAt(final Square square, final Piece piece) {
		setPieceAt(square.getColumn(), square.getRow(), piece);
	}

	public Piece getPieceAt(final Square square) {
		return getPieceAt(square.getColumn(), square.getRow());
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean hasSentePawnOnColumn(final int column) {
		for (Piece piece : board[column - 1]) {
			if (piece != null && piece.isSentePiece() && piece.getPieceType() == PieceType.PAWN) {
				return true;
			}
		}
		return false;
	}

	public ShogiBoardState opposite() {
		// TODO Auto-generated method stub
		return null;
	}
}
