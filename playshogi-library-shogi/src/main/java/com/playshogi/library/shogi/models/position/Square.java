package com.playshogi.library.shogi.models.position;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a square on the board. 1,1 is the top right, as in shogi.
 */
public class Square implements Comparable<Square> {
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

    public Optional<Square> above() {
        if (row == 1) return Optional.empty();

        return Optional.of(Square.of(column, row - 1));
    }

    public Optional<Square> below() {
        if (row == 9) return Optional.empty();

        return Optional.of(Square.of(column, row + 1));
    }

    public Optional<Square> left() {
        if (column == 9) return Optional.empty();

        return Optional.of(Square.of(column + 1, row));
    }

    public Optional<Square> right() {
        if (column == 1) return Optional.empty();

        return Optional.of(Square.of(column - 1, row));
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + column;
        result = prime * result + row;
        return result;
    }

    @Override
    public int compareTo(Square s) {
        if (column == s.column)
            return row - s.row;
        return column - s.column;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return compareTo((Square) obj) == 0;
    }

    @Override
    public String toString() {
        return column + String.valueOf(Character.toChars('a' + row - 1));
    }
}
