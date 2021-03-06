package com.playshogi.library.shogi.models.formats.psn;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;

public class PsnUtil {

    public static PieceType pieceFromChar(final char x) {
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

    public static char pieceTypeToChar(final PieceType x) {
        switch (x) {
            case PAWN:
                return 'P';
            case LANCE:
                return 'L';
            case KNIGHT:
                return 'N';
            case SILVER:
                return 'S';
            case GOLD:
                return 'G';
            case BISHOP:
                return 'B';
            case ROOK:
                return 'R';
            case KING:
                return 'K';
            default:
                throw new IllegalStateException("Unexpected value: " + x);
        }
    }

    public static String pieceToString(final Piece piece) {
        if (piece.isPromoted()) {
            return "+" + pieceTypeToChar(piece.getPieceType());
        } else {
            return String.valueOf(pieceTypeToChar(piece.getPieceType()));
        }
    }

    public static char columnNumber2Char(final int col) {
        return (char) (col + '0');
    }

    public static int char2ColumnNumber(final char colChar) {
        return colChar - '0';
    }

    public static char rowNumber2Char(final int row) {
        return (char) ('a' + (row - 1));
    }

    public static int char2RowNumber(final char row) {
        return row - 'a' + 1;
    }

}
