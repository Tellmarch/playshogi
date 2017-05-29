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
import com.playshogi.library.shogi.models.position.KomadaiState;
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
			if (l.isEmpty() || l.startsWith("#")) {
				l = lineReader.nextLine();
				continue;
			}

			if (l.equals("先手番")) {
				// sente to play?
				l = lineReader.nextLine();
				continue;
			}

			String[] sp = l.split("：", 2);
			if (sp.length < 2) {
				System.out.println("WARNING : unable to parse line " + l + " in file " + "???" + " , ignored.");
				l = lineReader.nextLine();
				continue;
			}
			String field = sp[0];
			String value = sp[1];
			if (field.equals("開始日時")) {
				date = value;
			} else if (field.equals("棋戦")) {
				tournament = value;
			} else if (field.equals("終了日時")) {
				// ending time? ignore.
			} else if (field.equals("戦型")) {
				opening = value;
			} else if (field.equals("場所")) {
				place = value;
			} else if (field.equals("持ち時間") || field.equals("対局日")) {
				time = value;
			} else if (field.equals("手合割")) {
				handicap = value;
				if (!value.startsWith("平手")) {
					// TODO
					// System.out.println("Handicap game, we ignore for
					// now");
					return null;
				}
			} else if (field.equals("後手") || field.equals("上手")) { // gote /
																	// handicap
																	// giver
				gote = value;
			} else if (field.equals("先手") || field.equals("下手")) { // sente /
																	// handicap
																	// receiver
				sente = value;
			} else if (field.equals("備考")) {
				// TODO : what is it?
			} else if (field.equals("作者")) {
				// TODO: author
			} else if (field.equals("作意")) {
				// TODO: conception
			} else if (field.equals("発表誌")) {
				// TODO: magazine
			} else if (field.equals("目安時間")) {
				// TODO: estimated time
			} else if (field.equals("思考時間")) {
				// TODO: think time
			} else if (field.equals("詰手数")) {
				// TODO: nr of moves
			} else if (field.equals("表題")) {
				// TODO : what is it?
			} else if (field.equals("消費時間")) {
				// Time used : ignored.
			} else if (field.equals("後手の持駒")) {
				// gote pieces in hand
				startingPosition = new ShogiPosition();
				KomadaiState komadai = startingPosition.getGoteKomadai();
				readPiecesInHand(value, komadai);
				l = lineReader.nextLine();
				l = lineReader.nextLine();
				for (int row = 1; row <= 9; row++) {
					l = lineReader.nextLine();
					int pos = 1;
					for (int column = 9; column >= 1; column--) {
						PieceParsingResult pieceParsingResult = readPiece(l, pos, true);
						pos = pieceParsingResult.nextPosition;
						startingPosition.getShogiBoardState().setPieceAt(Square.of(column, row), pieceParsingResult.piece);
					}
				}
				l = lineReader.nextLine();
			} else if (field.equals("先手の持駒")) {
				// sente pieces in hand
				KomadaiState komadai = startingPosition.getSenteKomadai();
				readPiecesInHand(value, komadai);
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
		GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<ShogiPosition>(new ShogiRulesEngine(), gameTree, startingPosition);

		GameResult gameResult = GameResult.UNKNOWN;

		boolean senteToMove = true;
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
			curMove = fromKifString(move, gameNavigation.getPosition(), prevMove, senteToMove);

			if (curMove == null) {
				System.out.println("Error parsing move in line " + line + "in file " + "???");
				break;
			}

			if (curMove instanceof SpecialMove) {
				SpecialMove specialMove = (SpecialMove) curMove;
				if (specialMove.getSpecialMoveType() == SpecialMoveType.RESIGN) {
					gameResult = senteToMove ? GameResult.SENTE_WIN : GameResult.GOTE_WIN;
				}
			}

			gameNavigation.addMove(curMove);
			senteToMove = !senteToMove;
			prevMove = curMove;
		}

		gameNavigation.moveToStart();
		GameInformation gameInformation = new GameInformation();
		gameInformation.setSente(sente);
		gameInformation.setGote(gote);
		gameInformation.setVenue(place);
		gameInformation.setDate(date);
		return new GameRecord(gameInformation, gameTree, gameResult);
	}

	private void readPiecesInHand(final String value, final KomadaiState komadai) {
		if (value.equals("なし")) {
			// nothing in hand
			return;
		}
		String[] piecesInHandStrings = value.split("　");
		for (String pieceString : piecesInHandStrings) {
			PieceParsingResult pieceParsingResult = readPiece(pieceString, 0, true);
			int number;
			if (pieceString.length() == 1) {
				number = 1;
			} else if (pieceString.length() == 2) {
				number = getNumberFromJapanese(pieceString.charAt(1));
			} else if (pieceString.length() == 3 && pieceString.charAt(1) == '十') {
				number = 10 + getNumberFromJapanese(pieceString.charAt(2));
			} else {
				throw new IllegalArgumentException("Error reading pieces in hand: " + value);
			}
			komadai.setPiecesOfType(pieceParsingResult.piece.getPieceType(), number);
		}
	}

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
			int row = getNumberFromJapanese(str.charAt(1));
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
		case '龍':
			piece = Piece.getPiece(PieceType.ROOK, sente, true);
			break;
		case '杏':
			piece = Piece.getPiece(PieceType.LANCE, sente, true);
			break;
		case '圭':
			piece = Piece.getPiece(PieceType.KNIGHT, sente, true);
			break;
		case '全':
			piece = Piece.getPiece(PieceType.SILVER, sente, true);
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

	public static int getNumberFromJapanese(final char rowChar) {
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
		case '十':
			return 10;
		}
		throw new IllegalArgumentException("Illegal row number: " + rowChar);
	}

}
