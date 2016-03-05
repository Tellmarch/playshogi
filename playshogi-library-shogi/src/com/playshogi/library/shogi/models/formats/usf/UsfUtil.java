package com.playshogi.library.shogi.models.formats.usf;

import com.playshogi.library.shogi.models.Piece;

public class UsfUtil {

	public static char columnNumber2Char(final int col) {
		return (char) (col + '0');
	}

	public static int char2ColumnNumber(final char colChar) {
		return colChar - '0';
	}

	public static char rowNumber2Char(final int row) {
		return (char) ('a' + (row - 1));
	}

	public static char rowNumber2Char(final int row, final boolean promote) {
		if (promote) {
			return (char) ('A' + (row - 1));
		} else {
			return (char) ('a' + (row - 1));
		}
	}

	public static int char2RowNumber(final char row) {
		if (row <= 'Z') {
			return row - 'A' + 1;
		} else {
			return row - 'a' + 1;
		}
	}

	static public boolean promote(final char p) {
		return p <= 'Z';
	}

	public static Piece pieceFromChar(final char x) {
		switch (x) {
		case 'P':
			return Piece.SENTE_PAWN;
		case 'L':
			return Piece.SENTE_LANCE;
		case 'N':
			return Piece.SENTE_KNIGHT;
		case 'S':
			return Piece.SENTE_SILVER;
		case 'G':
			return Piece.SENTE_GOLD;
		case 'B':
			return Piece.SENTE_BISHOP;
		case 'R':
			return Piece.SENTE_ROOK;
		case 'K':
			return Piece.SENTE_KING;
		case 'p':
			return Piece.GOTE_PAWN;
		case 'l':
			return Piece.GOTE_LANCE;
		case 'n':
			return Piece.GOTE_KNIGHT;
		case 's':
			return Piece.GOTE_SILVER;
		case 'g':
			return Piece.GOTE_GOLD;
		case 'b':
			return Piece.GOTE_BISHOP;
		case 'r':
			return Piece.GOTE_ROOK;
		case 'k':
			return Piece.GOTE_KING;
		}
		return null;
	}

	public static String pieceToString(final Piece x) {
		switch (x) {
		case GOTE_BISHOP:
			return "b";
		case GOTE_GOLD:
			return "g";
		case GOTE_KING:
			return "k";
		case GOTE_KNIGHT:
			return "n";
		case GOTE_LANCE:
			return "l";
		case GOTE_PAWN:
			return "p";
		case GOTE_PROMOTED_BISHOP:
			return "+b";
		case GOTE_PROMOTED_KNIGHT:
			return "+n";
		case GOTE_PROMOTED_LANCE:
			return "+l";
		case GOTE_PROMOTED_PAWN:
			return "+p";
		case GOTE_PROMOTED_ROOK:
			return "+r";
		case GOTE_PROMOTED_SILVER:
			return "+s";
		case GOTE_ROOK:
			return "r";
		case GOTE_SILVER:
			return "s";
		case SENTE_BISHOP:
			return "B";
		case SENTE_GOLD:
			return "G";
		case SENTE_KING:
			return "K";
		case SENTE_KNIGHT:
			return "N";
		case SENTE_LANCE:
			return "L";
		case SENTE_PAWN:
			return "P";
		case SENTE_PROMOTED_BISHOP:
			return "+B";
		case SENTE_PROMOTED_KNIGHT:
			return "+N";
		case SENTE_PROMOTED_LANCE:
			return "+L";
		case SENTE_PROMOTED_PAWN:
			return "+P";
		case SENTE_PROMOTED_ROOK:
			return "+R";
		case SENTE_PROMOTED_SILVER:
			return "+S";
		case SENTE_ROOK:
			return "R";
		case SENTE_SILVER:
			return "S";
		default:
			return "";

		}
	}
}
