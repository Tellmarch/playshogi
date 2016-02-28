package com.playshogi.library.models;

public class Square {
	private final int row;
	private final int column;

	public Square(final int row, final int column) {
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

}
