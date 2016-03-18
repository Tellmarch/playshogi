package com.playshogi.library.shogi.files.kif;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playshogi.library.models.Move;
import com.playshogi.library.models.Square;
import com.playshogi.library.models.games.Game;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.moves.SpecialMove;
import com.playshogi.library.shogi.models.moves.SpecialMoveType;
import com.playshogi.library.shogi.models.moves.ToSquareMove;

public class KifFormat {

	public Game read(final InputStream in) throws IOException {
		boolean bsente = true;
		Move curMove = null;
		Move prevMove = null;
		Scanner s = null;
		try {
			// Kif file use the encoding MS932
			s = new Scanner(in, "MS932");
			// s.useDelimiter("[：\n]");
			// System.out.println(s.next());
			String l = s.nextLine();
			String date = "1000-1-1";
			String tournament = "UNKNOWN";
			String opening = "UNKNOWN";
			String place = "UNKNOWN";
			String time = "UNKNOWN";
			String handicap = "UNKNOWN";
			String gote = "UNKNOWN";
			String sente = "UNKNOWN";
			while (!l.startsWith("手数")) {
				String[] sp = l.split("：", 2);
				if (sp.length < 2) {
					System.out.println("WARNING : unable to parse line " + l + " in file " + "???" + " , ignored.");
					l = s.nextLine();
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
					if (!sn2.equals("平手")) {
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
					l = s.nextLine();
					l = s.nextLine();
					for (int row = 1; row <= 9; row++) {
						l = s.nextLine();
						for (int column = 1; column <= 9; column++) {
							String square = l.substring(1 + column * 2, 1 + column * 2 + 2);
							System.out.println(square);
						}
					}
				} else if (sn.equals("先手の持駒")) {
					// sente pieces in hand
				} else {
					System.out.println("WARNING : unknown field " + l + " in file " + "???" + " , ignored !");
				}
				l = s.nextLine();
			}
			// s.next();
			// s.useDelimiter("[ \r\n]");
			GameTree gameTree = new GameTree();
			int move = 0;
			while (s.hasNextLine()) {
				String t = s.nextLine();
				if (t.isEmpty()) {
					continue;
				}
				if (t.startsWith("*")) {
					continue;
				}
				t = t.trim();
				String[] ts = t.split(" ", 2);
				int i;
				try {
					i = new Integer(ts[0]);
				} catch (Exception ex) {
					break;
				}
				if (i != move + 1 || ts.length < 2) {
					throw new Exception("Error after move " + move);
				}
				move++;
				t = ts[1];
				// System.out.println(t);
				curMove = Move.fromKifString(prevMove, t, bsente);
				// System.out.println(curMove.getPiece());
				if (curMove == null) {
					System.out.println("Error parsing move in line " + t + "in file " + "???");
					break;
				}
				if (!Rules.isMoveValid(curMove, gameTree.getCurrentPos())) {
					System.out.println("WARNING : move " + move + " : " + curMove + " is invalid ! (" + "???" + ")");
				}
				gameTree.addMove(curMove);
				// System.out.println(gameTree.getCurrentPos().toSFEN());
				bsente = !bsente;
				prevMove = curMove;
			}
			int result = 0;
			switch (curMove.getSpecialType()) {
			case Move.SPECIAL_BREK:
				result = Game.RESULT_BREAK;
				break;
			case Move.SPECIAL_JISO:
				result = Game.RESULT_JISHOGI;
				break;
			case Move.SPECIAL_REPT:
				result = Game.RESULT_SENNICHITE;
				break;
			case Move.SPECIAL_RSGN:
				if (bsente)
					result = Game.RESULT_SENTE;
				else
					result = Game.RESULT_GOTE;
			}
			return new Game(sente, gote, date, 0, gameTree, 0, opening, tournament, result);
		} catch (IOException ex) {
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
			throw (ex);
		} catch (Exception ex) {
			System.out.println("Error while parsing the file " + "???" + " !");
			if (!(ex instanceof java.util.NoSuchElementException))
				Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
			throw (new IOException(ex));
		} finally {
			if (s != null) {
				s.close();
			}
		}
	}

	public static ShogiMove fromKifString(final ShogiMove previousMove, final String str, final boolean sente) {
		try {

			if (str.startsWith("投了")) {
				return new SpecialMove(sente, SpecialMoveType.RESIGN);
			} else if (str.startsWith("千日手")) {
				return new SpecialMove(sente, SpecialMoveType.SENNICHITE);
			} else if (str.startsWith("持将棋")) {
				return new SpecialMove(sente, SpecialMoveType.JISHOGI);
			} else if (str.startsWith("中断")) {
				return new SpecialMove(sente, SpecialMoveType.BREAK);
			}

			// First, read destination coordinates
			Square toSquare;
			char firstChar = str.charAt(0);
			char secondChar = str.charAt(1);
			if (firstChar == '同') {
				// capture
				toSquare = ((ToSquareMove) previousMove).getToSquare();
			} else if (firstChar >= '1' && firstChar <= '9' && secondChar >= '一' && secondChar <= '九') {
				int column = firstChar - '1' + 1;
				int row = secondChar - '一' + 1;
				toSquare = Square.of(column, row);
			} else {
				throw new IllegalArgumentException("Unrecognized move: " + str);
			}

			int piece = 0;
			pos++;
			switch (str.charAt(pos)) {
			case '歩':
				piece = Piece.PIECE_PAWN1;
				break;
			case '香':
				piece = Piece.PIECE_LANCE1;
				break;
			case '桂':
				piece = Piece.PIECE_KNIGHT1;
				break;
			case '銀':
				piece = Piece.PIECE_SILVER1;
				break;
			case '金':
				piece = Piece.PIECE_GOLD1;
				break;
			case '角':
				piece = Piece.PIECE_BISHOP1;
				break;
			case '飛':
				piece = Piece.PIECE_ROOK1;
				break;
			case '王':
				piece = Piece.PIECE_KING1;
				break;
			case 'と':
				piece = Piece.PIECE_PPAWN1;
				break;
			case '馬':
				piece = Piece.PIECE_PBISHOP1;
				break;
			case '竜':
				piece = Piece.PIECE_PROOK1;
				break;
			case '成':
				// Special case : promoted piece...
				pos++;
				switch (str.charAt(pos)) {
				case '香':
					piece = Piece.PIECE_PLANCE1;
					break;
				case '桂':
					piece = Piece.PIECE_PKNIGHT1;
					break;
				case '銀':
					piece = Piece.PIECE_PSILVER1;
					break;
				}
				break;
			}
			if (!sente) {
				piece = Piece.changeSide(piece);
			}
			pos++;
			boolean drop = false;
			boolean promote = false;
			char c = str.charAt(pos);
			if (c == '成') {
				// Promote
				promote = true;
				pos++;
				c = str.charAt(pos);
			}
			int row2 = 0;
			int column2 = 0;
			if (c == '打') {
				// Drop
				drop = true;
				if (sente) {
					column2 = -1;
					row2 = -1;
				} else {
					column2 = -2;
					row2 = -2;
				}
			} else if (c == '(') {
				// Reading the destination square
				pos++;
				c = str.charAt(pos);
				column2 = 8 - (c - '1');
				pos++;
				c = str.charAt(pos);
				row2 = c - '1';
			} else {
				throw new Exception("Error reading the move " + str);
			}

			Move m = new Move(column2, row2, column, row, promote, piece, drop);
			// System.out.println(m);
			return m;
		} catch (

		Exception ex) {
			System.out.println("Error parsing the move " + str);
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

}
