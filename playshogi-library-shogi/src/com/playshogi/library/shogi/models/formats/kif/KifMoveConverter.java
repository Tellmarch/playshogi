package com.playshogi.library.shogi.models.formats.kif;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.formats.kif.KifUtils.PieceParsingResult;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.moves.SpecialMove;
import com.playshogi.library.shogi.models.moves.SpecialMoveType;
import com.playshogi.library.shogi.models.moves.ToSquareMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class KifMoveConverter {

	public static ShogiMove fromKifString(final String str, final ShogiPosition shogiPosition, final ShogiMove previousMove, final boolean sente) {

		if (str.startsWith("投了")) {
			return new SpecialMove(sente, SpecialMoveType.RESIGN);
		} else if (str.startsWith("千日手")) {
			return new SpecialMove(sente, SpecialMoveType.SENNICHITE);
		} else if (str.startsWith("持将棋")) {
			return new SpecialMove(sente, SpecialMoveType.JISHOGI);
		} else if (str.startsWith("中断")) {
			return new SpecialMove(sente, SpecialMoveType.BREAK);
		} else if (str.startsWith("反則勝ち")) { // what is this?
			return new SpecialMove(sente, SpecialMoveType.OTHER);
		}

		int pos = 0;
		// First, read destination coordinates
		Square toSquare;
		char firstChar = str.charAt(0);
		if (firstChar == '同') {
			// capture
			toSquare = ((ToSquareMove) previousMove).getToSquare();
			pos++;
		} else if (firstChar >= '１' && firstChar <= '９') {
			int column = firstChar - '１' + 1;
			int row = KifUtils.getNumberFromJapanese(str.charAt(1));
			toSquare = Square.of(column, row);
			pos += 2;
		} else {
			throw new IllegalArgumentException("Unrecognized move: " + str);
		}

		if (str.charAt(pos) == '　') {
			pos++;
		}

		PieceParsingResult pieceParsingResult = KifUtils.readPiece(str, pos, sente);
		Piece piece = pieceParsingResult.piece;
		pos = pieceParsingResult.nextPosition;

		boolean promote = false;
		char c = str.charAt(pos);
		if (c == '成') {
			// Promote
			promote = true;
			c = str.charAt(++pos);
		}
		if (c == '不') {
			c = str.charAt(++pos);
			if (c == '成') {
				// Not Promote
				promote = false;
				c = str.charAt(++pos);
			} else {
				throw new IllegalArgumentException("Error reading the move " + str);
			}
		}
		if (c == '打') {
			// Drop
			return new DropMove(sente, piece.getPieceType(), toSquare);
		} else if (c == '(') {
			// Reading the destination square
			int column2 = str.charAt(++pos) - '0';
			int row2 = str.charAt(++pos) - '0';
			Square fromSquare = Square.of(column2, row2);

			if (shogiPosition.getShogiBoardState().getPieceAt(toSquare) == null) {
				return new NormalMove(piece, fromSquare, toSquare, promote);
			} else {
				return new CaptureMove(piece, fromSquare, toSquare, promote, shogiPosition.getShogiBoardState().getPieceAt(toSquare));
			}

		} else {
			throw new IllegalArgumentException("Error reading the move " + str);
		}
	}

	public static String toKifString(final ShogiMove move) {

		if (move instanceof NormalMove) {
			NormalMove normalMove = (NormalMove) move;

			return "" + KifUtils.getJapaneseWesternNumber(normalMove.getToSquare().getColumn()) + KifUtils.getJapaneseNumber(normalMove.getToSquare().getRow())
					+ KifUtils.getJapanesePieceSymbol(normalMove.getPiece()) + (normalMove.isPromote() ? "成 (" : " (") + normalMove.getFromSquare().getColumn()
					+ normalMove.getFromSquare().getRow() + ")";

		} else if (move instanceof DropMove) {
			DropMove dropMove = (DropMove) move;

			return "" + KifUtils.getJapaneseWesternNumber(dropMove.getToSquare().getColumn()) + KifUtils.getJapaneseNumber(dropMove.getToSquare().getRow())
					+ KifUtils.getJapanesePieceSymbol(dropMove.getPieceType()) + "打";

		} else if (move instanceof SpecialMove) {
			SpecialMove specialMove = (SpecialMove) move;

			// TODO
			return specialMove.getUsfString();

		} else {
			throw new IllegalArgumentException("Unknown move type " + move);
		}
	}

	public static String toKifStringShort(final ShogiMove move) {

		if (move instanceof NormalMove) {
			NormalMove normalMove = (NormalMove) move;

			return "" + KifUtils.getJapaneseWesternNumber(normalMove.getToSquare().getColumn()) + KifUtils.getJapaneseNumber(normalMove.getToSquare().getRow())
					+ KifUtils.getJapanesePieceSymbol(normalMove.getPiece()) + (normalMove.isPromote() ? "成" : "");

		} else if (move instanceof DropMove) {
			DropMove dropMove = (DropMove) move;

			return "" + KifUtils.getJapaneseWesternNumber(dropMove.getToSquare().getColumn()) + KifUtils.getJapaneseNumber(dropMove.getToSquare().getRow())
					+ KifUtils.getJapanesePieceSymbol(dropMove.getPieceType()) + "打";

		} else if (move instanceof SpecialMove) {
			SpecialMove specialMove = (SpecialMove) move;

			// TODO
			return specialMove.getUsfString();

		} else {
			throw new IllegalArgumentException("Unknown move type " + move);
		}
	}
}
