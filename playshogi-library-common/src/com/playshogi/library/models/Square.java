package com.playshogi.library.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a square on the board. 1,1 is the top right, as in shogi.
 *
 */
public class Square {
	private final int column;
	private final int row;

	private Square(final int column, final int row) {
		this.column = column;
		this.row = row;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public static Square of(final int column, final int row) {
		return new Square(column, row);
	}

	public Square opposite() {
		return of(10 - column, 10 - row);
	}

	public static List<Square> opposite(final List<Square> squares) {
		ArrayList<Square> result = new ArrayList<>(squares.size());
		for (Square square : squares) {
			result.add(square.opposite());
		}
		return result;
	}
}
