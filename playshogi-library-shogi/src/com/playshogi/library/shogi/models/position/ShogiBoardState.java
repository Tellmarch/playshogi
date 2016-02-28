package com.playshogi.library.shogi.models.position;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;

public class ShogiBoardState {

	private final int width;
	private final int height;

	private final Piece[][] board;

	public ShogiBoardState(final int width, final int height) {
		this.width = width;
		this.height = height;

		board = new Piece[width][height];
	}

	public Piece getPieceAt(final Square square) {
		return board[square.getColumn() - 1][square.getRow() - 1];
	}

	public void setPieceAt(final Square square, final Piece piece) {
		board[square.getColumn() - 1][square.getRow() - 1] = piece;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
