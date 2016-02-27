package com.playshogi.library.models;

public class Square {
	private final short row;
	private final short column;

	public Square(final short row, final short column) {
		this.row = row;
		this.column = column;
	}

	public short getRow() {
		return row;
	}

	public short getColumn() {
		return column;
	}

}
