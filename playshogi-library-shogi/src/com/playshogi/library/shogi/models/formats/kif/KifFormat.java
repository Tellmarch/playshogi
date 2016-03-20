package com.playshogi.library.shogi.models.formats.kif;

import com.playshogi.library.models.Square;
import com.playshogi.library.models.record.GameInformation;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.models.record.GameResult;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.formats.sfen.GameRecordFormat;
import com.playshogi.library.shogi.models.formats.sfen.LineReader;
import com.playshogi.library.shogi.models.formats.sfen.StringLineReader;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.moves.SpecialMove;
import com.playshogi.library.shogi.models.moves.SpecialMoveType;
import com.playshogi.library.shogi.models.moves.ToSquareMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

public enum KifFormat implements GameRecordFormat {
	INSTANCE;

	@Override
	public GameRecord read(final String kifString) {
		return read(new StringLineReader(kifString));
	}

	@Override
	public GameRecord read(final LineReader lineReader) {
		String date = "1000-1-1";
		String tournament = "UNKNOWN";
		String opening = "UNKNOWN";
		String place = "UNKNOWN";
		String time = "UNKNOWN";
		String handicap = "UNKNOWN";
		String gote = "UNKNOWN";
		String sente = "UNKNOWN";

		ShogiPosition startingPosition = null;

		String l = lineReader.nextLine();
		while (!l.startsWith("手数")) {
			l = l.trim();
			if (l.startsWith("#")) {
				l = lineReader.nextLine();
				continue;
			}
			String[] sp = l.split("：", 2);
			if (sp.length < 2) {
				System.out.println("WARNING : unable to parse line " + l + " in file " + "???" + " , ignored.");
				l = lineReader.nextLine();
				continue;
			}
			String sn = sp[0];
			String sn2 = sp[1];
			if (sn.equals("開始日時")) {
				date = sn2;
			} else if (sn.equals("棋戦")) {
				tournament = sn2;
			} else if (sn.equals("終了日時")) {
				// ending time? ignore.
			} else if (sn.equals("戦型")) {
				opening = sn2;
			} else if (sn.equals("場所")) {
				place = sn2;
			} else if (sn.equals("持ち時間") || sn.equals("対局日")) {
				time = sn2;
			} else if (sn.equals("手合割")) {
				handicap = sn2;
				if (!sn2.startsWith("平手")) {
					// TODO
					// System.out.println("Handicap game, we ignore for
					// now");
					return null;
				}
			} else if (sn.equals("後手") || sn.equals("上手")) {
				gote = sn2;
			} else if (sn.equals("先手") || sn.equals("下手")) {
				sente = sn2;
			} else if (sn.equals("備考")) {
				// TODO : what is it?
			} else if (sn.equals("表題")) {
				// TODO : what is it?
			} else if (sn.equals("消費時間")) {
				// Time used : ignored.
			} else if (sn.equals("後手の持駒")) {
				// gote pieces in hand
				l = lineReader.nextLine();
				l = lineReader.nextLine();
				startingPosition = new ShogiPosition();
				for (int row = 1; row <= 9; row++) {
					l = lineReader.nextLine();
					int pos = 1;
					for (int column = 9; column >= 1; column--) {
						PieceParsingResult pieceParsingResult = readPiece(l, pos, true);
						pos = pieceParsingResult.nextPosition;
						startingPosition.getShogiBoardState().setPieceAt(Square.of(column, row),
								pieceParsingResult.piece);
					}
				}
			} else if (sn.equals("先手の持駒")) {
				// sente pieces in hand
			} else {
				System.out.println("WARNING : unknown field " + l + " in file " + "???" + " , ignored !");
			}
			l = lineReader.nextLine();
		}
		// s.next();
		// s.useDelimiter("[ \r\n]");

		if (startingPosition == null) {
			startingPosition = new ShogiInitialPositionFactory().createInitialPosition();
		}
		GameTree gameTree = new GameTree(startingPosition);
		GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<ShogiPosition>(new ShogiRulesEngine(),
				gameTree, startingPosition);

		boolean bsente = true;
		ShogiMove curMove = null;
		ShogiMove prevMove = null;
		int moveNumber = 1;
		while (lineReader.hasNextLine()) {
			String line = lineReader.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}
			if (line.startsWith("*")) {
				continue;
			}
			String[] ts = line.split(" ", 2);
			int i;
			try {
				i = new Integer(ts[0]);
			} catch (Exception ex) {
				break;
			}
			if (i != moveNumber || ts.length < 2) {
				throw new IllegalArgumentException("Error after move " + moveNumber);
			}
			moveNumber++;
			String move = ts[1];
			curMove = fromKifString(move, gameNavigation.getPosition(), prevMove, bsente);
			// System.out.println(curMove.getPiece());
			if (curMove == null) {
				System.out.println("Error parsing move in line " + line + "in file " + "???");
				break;
			}
			gameNavigation.addMove(curMove, true);
			bsente = !bsente;
			prevMove = curMove;
		}

		gameNavigation.moveToStart();
		GameInformation gameInformation = new GameInformation();
		GameResult gameResult = new GameResult();
		return new GameRecord(gameInformation, gameTree, gameResult);
	}

	public static ShogiMove fromKifString(final String str, final ShogiPosition shogiPosition,
			final ShogiMove previousMove, final boolean sente) {

		if (str.startsWith("投了")) {
			return new SpecialMove(sente, SpecialMoveType.RESIGN);
		} else if (str.startsWith("千日手")) {
			return new SpecialMove(sente, SpecialMoveType.SENNICHITE);
		} else if (str.startsWith("持将棋")) {
			return new SpecialMove(sente, SpecialMoveType.JISHOGI);
		} else if (str.startsWith("中断")) {
			return new SpecialMove(sente, SpecialMoveType.BREAK);
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
			int row = getRowNumber(str.charAt(1));
			toSquare = Square.of(column, row);
			pos += 2;
		} else {
			throw new IllegalArgumentException("Unrecognized move: " + str);
		}

		if (str.charAt(pos) == '　') {
			pos++;
		}

		PieceParsingResult pieceParsingResult = readPiece(str, pos, sente);
		Piece piece = pieceParsingResult.piece;
		pos = pieceParsingResult.nextPosition;

		boolean promote = false;
		char c = str.charAt(pos);
		if (c == '成') {
			// Promote
			promote = true;
			c = str.charAt(++pos);
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
				return new CaptureMove(piece, fromSquare, toSquare, promote,
						shogiPosition.getShogiBoardState().getPieceAt(toSquare));
			}

		} else {
			throw new IllegalArgumentException("Error reading the move " + str);
		}
	}

	@Override
	public String write(final GameRecord gameRecord) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String write(final GameTree gameTree) {
		throw new UnsupportedOperationException();
	}

	private static PieceParsingResult readPiece(final String str, int pos, boolean sente) {

		while (str.charAt(pos) == ' ') {
			pos++;
		}

		if (str.charAt(pos) == 'v') {
			sente = false;
			pos++;
		}

		Piece piece;
		switch (str.charAt(pos)) {
		case '・':
			piece = null;
			break;
		case '歩':
			piece = Piece.getPiece(PieceType.PAWN, sente);
			break;
		case '香':
			piece = Piece.getPiece(PieceType.LANCE, sente);
			break;
		case '桂':
			piece = Piece.getPiece(PieceType.KNIGHT, sente);
			break;
		case '銀':
			piece = Piece.getPiece(PieceType.SILVER, sente);
			break;
		case '金':
			piece = Piece.getPiece(PieceType.GOLD, sente);
			break;
		case '角':
			piece = Piece.getPiece(PieceType.BISHOP, sente);
			break;
		case '飛':
			piece = Piece.getPiece(PieceType.ROOK, sente);
			break;
		case '王':
		case '玉':
			piece = Piece.getPiece(PieceType.KING, sente);
			break;
		case 'と':
			piece = Piece.getPiece(PieceType.PAWN, sente, true);
			break;
		case '馬':
			piece = Piece.getPiece(PieceType.BISHOP, sente, true);
			break;
		case '竜':
			piece = Piece.getPiece(PieceType.ROOK, sente, true);
			break;
		case '成': {
			// Special case : promoted piece...
			pos++;
			switch (str.charAt(pos)) {
			case '香':
				piece = Piece.getPiece(PieceType.LANCE, sente, true);
				break;
			case '桂':
				piece = Piece.getPiece(PieceType.KNIGHT, sente, true);
				break;
			case '銀':
				piece = Piece.getPiece(PieceType.SILVER, sente, true);
				break;
			default:
				throw new IllegalArgumentException("Error reading the move " + str);
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Error reading the piece " + str);
		}
		return new PieceParsingResult(piece, pos + 1);
	}

	private static class PieceParsingResult {
		public Piece piece;
		public int nextPosition;

		public PieceParsingResult(final Piece piece, final int nextPosition) {
			this.piece = piece;
			this.nextPosition = nextPosition;
		}
	}

	public static int getRowNumber(final char rowChar) {
		switch (rowChar) {
		case '一':
			return 1;
		case '二':
			return 2;
		case '三':
			return 3;
		case '四':
			return 4;
		case '五':
			return 5;
		case '六':
			return 6;
		case '七':
			return 7;
		case '八':
			return 8;
		case '九':
			return 9;
		}
		throw new IllegalArgumentException("Illegal row number: " + rowChar);
	}

}
