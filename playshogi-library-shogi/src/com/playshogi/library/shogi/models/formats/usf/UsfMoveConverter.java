package com.playshogi.library.shogi.models.formats.usf;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.moves.SpecialMove;
import com.playshogi.library.shogi.models.moves.SpecialMoveType;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class UsfMoveConverter {

	public static final String[] specialStrings = { "", "DUMY", "SLNT", "RSGN", "BREK", "JISO", "TIME", "FOUL", "VICT",
			"MATE", "REPT", "NMAT", "++++", "++..", "+...", "====", "-...", "--..", "----", "=88=", "+88-" };

	public static final SpecialMoveType[] specialTypes = { null, null, null, SpecialMoveType.RESIGN, null,
			SpecialMoveType.JISHOGI, null, SpecialMoveType.ILLEGAL_MOVE, null, null, SpecialMoveType.SENNICHITE, null,
			null, null, null, null, null, null, null, null, null };

	/**
	 * Create a move from a 4 character long USF String.
	 */
	public static ShogiMove fromUsfString(final String usfMove, final ShogiPosition shogiPosition) {
		// Move description is always 4 char
		if (usfMove.length() != 4) {
			return null;
		}

		// Is it a special move?
		for (int i = 1; i < specialStrings.length; i++) {
			if (usfMove.equalsIgnoreCase(specialStrings[i])) {
				if (specialTypes[i] != null) {
					return new SpecialMove(shogiPosition.isSenteToPlay(), specialTypes[i]);
				} else {
					return new SpecialMove(shogiPosition.isSenteToPlay(), SpecialMoveType.OTHER);
				}
			}
		}

		// Probably not, let's figure out the coordinates
		int col1 = 0, col2, row1 = 0, row2;
		boolean promotion = false;
		boolean drop = false;
		Piece piece;
		if (usfMove.charAt(1) == '*') {
			// Drop
			drop = true;
			piece = UsfUtil.pieceFromChar(usfMove.charAt(0));
		} else {
			col1 = UsfUtil.char2ColumnNumber(usfMove.charAt(0));
			row1 = UsfUtil.char2RowNumber(usfMove.charAt(1));
			piece = shogiPosition.getPieceAt(Square.of(col1, row1));
		}
		if (usfMove.charAt(3) == '*') {
			// TODO
			// to komadai
			return null;
		} else {
			col2 = UsfUtil.char2ColumnNumber(usfMove.charAt(2));
			row2 = UsfUtil.char2RowNumber(usfMove.charAt(3));
			promotion = UsfUtil.promote(usfMove.charAt(3));
		}
		if (drop) {
			return new DropMove(piece.isSentePiece(), piece.getPieceType(), Square.of(col2, row2));
		} else {
			Piece capturedPiece = shogiPosition.getPieceAt(Square.of(col2, row2));
			if (capturedPiece == null) {
				return new NormalMove(piece, Square.of(col1, row1), Square.of(col2, row2), promotion);
			} else {
				return new CaptureMove(piece, Square.of(col1, row1), Square.of(col2, row2), promotion, capturedPiece);
			}
		}
	}

	public static String toUsfString(final ShogiMove move) {
		if (move instanceof DropMove) {
			return toUsfString((DropMove) move);
		} else if (move instanceof NormalMove) {
			return toUsfString((NormalMove) move);
		} else if (move instanceof SpecialMove) {
			return toUsfString((SpecialMove) move);
		} else {
			return null;
		}
	}

	private static String toUsfString(final DropMove move) {
		Piece piece = Piece.getPiece(move.getPieceType(), move.isSenteMoving());
		return UsfUtil.pieceToString(piece) + '*' + UsfUtil.columnNumber2Char(move.getToSquare().getColumn())
				+ UsfUtil.rowNumber2Char(move.getToSquare().getRow());
	}

	private static String toUsfString(final NormalMove move) {
		return "" + UsfUtil.columnNumber2Char(move.getFromSquare().getColumn())
				+ UsfUtil.rowNumber2Char(move.getFromSquare().getRow())
				+ UsfUtil.columnNumber2Char(move.getToSquare().getColumn())
				+ UsfUtil.rowNumber2Char(move.getToSquare().getRow(), move.isPromote());
	}

	private static String toUsfString(final SpecialMove move) {
		switch (move.getSpecialMoveType()) {
		case BREAK:
			return "BREK";
		case ILLEGAL_MOVE:
			return "FOUL";
		case JISHOGI:
			return "JISO";
		case RESIGN:
			return "RSGN";
		case SENNICHITE:
			return "REPT";
		default:
			return "SLNT";
		}

	}
}
