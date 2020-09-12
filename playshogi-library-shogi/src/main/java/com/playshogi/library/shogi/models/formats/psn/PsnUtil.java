package com.playshogi.library.shogi.models.formats.psn;

import com.playshogi.library.shogi.models.PieceType;

class PsnUtil {

    static PieceType pieceFromChar(final char x) {
        switch (x) {
            case 'P':
                return PieceType.PAWN;
            case 'L':
                return PieceType.LANCE;
            case 'N':
                return PieceType.KNIGHT;
            case 'S':
                return PieceType.SILVER;
            case 'G':
                return PieceType.GOLD;
            case 'B':
                return PieceType.BISHOP;
            case 'R':
                return PieceType.ROOK;
            case 'K':
                return PieceType.KING;
        }
        return null;
    }

    static char columnNumber2Char(final int col) {
        return (char) (col + '0');
    }

    static int char2ColumnNumber(final char colChar) {
        return colChar - '0';
    }

    static char rowNumber2Char(final int row) {
        return (char) ('a' + (row - 1));
    }

    static int char2RowNumber(final char row) {
        return row - 'a' + 1;
    }

}
