package com.playshogi.library.models;

/**
 * Represents a square on the board. 1,1 is the top right, as in shogi.
 *
 */
public class Square {
	private final int column;
	private final int row;

	public Square(final int column, final int row) {
		this.column = column;
		this.row = row;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

}
