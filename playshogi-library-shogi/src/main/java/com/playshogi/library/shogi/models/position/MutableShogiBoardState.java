package com.playshogi.library.shogi.models.position;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.formats.usf.UsfUtil;

import java.util.Optional;

public abstract class MutableShogiBoardState implements ShogiBoardState {

    public abstract void setPieceAt(int column, int row, Piece piece);

    public abstract void setPieceAt(Square square, Piece piece);

    @Override
    public int getLastRow() {
        return getHeight();
    }

    @Override
    public int getLastColumn() {
        return getWidth();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(600);
        builder.append("\n  9  8  7  6  5  4  3  2  1\n");
        for (int row = FIRST_ROW; row <= getLastRow(); row++) {
            builder.append("----------------------------\n");
            for (int column = FIRST_COLUMN; column <= getLastColumn(); column++) {
                builder.append('|');
                Optional<Piece> pieceAt = getPieceAt(10 - column, row);
                if (pieceAt.isPresent()) {
                    String pieceToString = UsfUtil.pieceToString(pieceAt.get());
                    if (pieceToString.length() == 1) {
                        builder.append(' ');
                    }
                    builder.append(pieceToString);
                } else {
                    builder.append("  ");
                }
            }
            builder.append("| ").append(Character.toChars('a' + (row - FIRST_ROW))).append('\n');
        }
        builder.append("----------------------------");

        return builder.toString();
    }
}
